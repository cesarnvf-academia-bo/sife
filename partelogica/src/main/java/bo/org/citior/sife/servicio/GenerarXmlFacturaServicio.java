package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.Factura;
import bo.org.citior.sife.dominio.FacturaDetalle;
import bo.org.citior.sife.excepcion.XmlFacturaInvalidoExcepcion;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Service
public class GenerarXmlFacturaServicio {

    private static final String XSD_RELATIVO = "recursos/facturaElectronicaCompraVenta.xsd";
    private static final String DS_NAMESPACE = "http://www.w3.org/2000/09/xmldsig#";
    private static final DateTimeFormatter FECHA_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ROOT)
            .withZone(ZoneOffset.UTC);

    public Document construir(Factura factura, List<FacturaDetalle> detalles) {
        if (factura == null) {
            throw new IllegalArgumentException("factura es obligatoria.");
        }
        if (detalles == null || detalles.isEmpty() || detalles.size() > 500) {
            throw new IllegalArgumentException("El detalle debe tener entre 1 y 500 líneas.");
        }

        try {
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            fabrica.setNamespaceAware(true);
            Document documento = fabrica.newDocumentBuilder().newDocument();

            Element raiz = documento.createElementNS(null, "facturaElectronicaCompraVenta");
            raiz.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi",
                    XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            raiz.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
                    "xsi:noNamespaceSchemaLocation", "facturaElectronicaCompraVenta.xsd");
            documento.appendChild(raiz);

            Element cabecera = documento.createElement("cabecera");
            raiz.appendChild(cabecera);
            construirCabecera(cabecera, factura);

            for (FacturaDetalle detalle : detalles) {
                Element elementoDetalle = documento.createElement("detalle");
                raiz.appendChild(elementoDetalle);
                construirDetalle(elementoDetalle, detalle);
            }

            Element firma = documento.createElementNS(DS_NAMESPACE, "ds:Signature");
            firma.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:ds", DS_NAMESPACE);
            raiz.appendChild(firma);

            return documento;
        } catch (ParserConfigurationException excepcion) {
            throw new XmlFacturaInvalidoExcepcion(
                    "No se pudo construir el documento XML: " + excepcion.getMessage());
        }
    }

    public void validar(Document documento) {
        if (documento == null) {
            throw new XmlFacturaInvalidoExcepcion("El documento XML es nulo.");
        }

        try {
            SchemaFactory fabricaEsquema =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema esquema = fabricaEsquema.newSchema(resolverXsd().toFile());
            Validator validador = esquema.newValidator();
            validador.validate(new DOMSource(documento));
        } catch (SAXParseException excepcion) {
            throw new XmlFacturaInvalidoExcepcion(
                    "Línea " + excepcion.getLineNumber()
                            + ", columna " + excepcion.getColumnNumber()
                            + ": " + excepcion.getMessage());
        } catch (SAXException | IOException excepcion) {
            throw new XmlFacturaInvalidoExcepcion(
                    "El XML de la factura no cumple el esquema: " + excepcion.getMessage());
        }
    }

    private void construirCabecera(Element cabecera, Factura factura) {
        agregar(cabecera, "nitEmisor", Long.toString(factura.getNitEmisor()));
        agregar(cabecera, "razonSocialEmisor", factura.getRazonSocialEmisor());
        agregar(cabecera, "municipio", factura.getMunicipio());
        agregarOpcional(cabecera, "telefono", factura.getTelefono());
        agregar(cabecera, "numeroFactura", Long.toString(factura.getNumeroFactura()));
        agregar(cabecera, "cuf", factura.getCuf());
        agregar(cabecera, "cufd", factura.getCufd());
        agregar(cabecera, "codigoSucursal", Integer.toString(factura.getCodigoSucursal()));
        agregar(cabecera, "direccion", factura.getDireccion());
        agregarOpcional(cabecera, "codigoPuntoVenta", factura.getCodigoPuntoVenta() == null
                ? null
                : Integer.toString(factura.getCodigoPuntoVenta()));
        agregar(cabecera, "fechaEmision", FECHA_FORMAT.format(factura.getFechaEmision()));
        agregar(cabecera, "nombreRazonSocial", factura.getNombreRazonSocial());
        agregar(cabecera, "codigoTipoDocumentoIdentidad",
                Integer.toString(factura.getCodigoTipoDocumentoIdentidad()));
        agregar(cabecera, "numeroDocumento", factura.getNumeroDocumento());
        agregarOpcional(cabecera, "complemento", factura.getComplemento());
        agregar(cabecera, "codigoCliente", factura.getCodigoCliente());
        agregar(cabecera, "codigoMetodoPago", Integer.toString(factura.getCodigoMetodoPago()));
        agregarOpcional(cabecera, "numeroTarjeta", factura.getNumeroTarjeta());
        agregar(cabecera, "montoTotal", decimal(factura.getMontoTotal()));
        agregar(cabecera, "montoTotalSujetoIva", decimal(factura.getMontoTotalSujetoIva()));
        agregar(cabecera, "codigoMoneda", Integer.toString(factura.getCodigoMoneda()));
        agregar(cabecera, "tipoCambio", decimal(factura.getTipoCambio()));
        agregar(cabecera, "montoTotalMoneda", decimal(factura.getMontoTotalMoneda()));
        agregarOpcional(cabecera, "montoGiftCard", decimal(factura.getMontoGiftCard()));
        agregarOpcional(cabecera, "descuentoAdicional", decimal(factura.getDescuentoAdicional()));
        agregarOpcional(cabecera, "codigoExcepcion", factura.getCodigoExcepcion() == null
                ? null
                : Integer.toString(factura.getCodigoExcepcion()));
        agregarOpcional(cabecera, "cafc", factura.getCafc());
        agregar(cabecera, "leyenda", factura.getLeyenda());
        agregar(cabecera, "usuario", factura.getUsuario());
        agregar(cabecera, "codigoDocumentoSector",
                Integer.toString(factura.getCodigoDocumentoSector()));
    }

    private void construirDetalle(Element detalle, FacturaDetalle linea) {
        agregar(detalle, "actividadEconomica", linea.getActividadEconomica());
        agregar(detalle, "codigoProductoSin", Long.toString(linea.getCodigoProductoSin()));
        agregar(detalle, "codigoProducto", linea.getCodigoProducto());
        agregar(detalle, "descripcion", linea.getDescripcion());
        agregar(detalle, "cantidad", decimal(linea.getCantidad()));
        agregar(detalle, "unidadMedida", Integer.toString(linea.getUnidadMedida()));
        agregar(detalle, "precioUnitario", decimal(linea.getPrecioUnitario()));
        agregarOpcional(detalle, "montoDescuento", decimal(linea.getMontoDescuento()));
        agregar(detalle, "subTotal", decimal(linea.getSubTotal()));
        agregarOpcional(detalle, "numeroSerie", linea.getNumeroSerie());
        agregarOpcional(detalle, "numeroImei", linea.getNumeroImei());
    }

    private void agregar(Element padre, String nombre, String valor) {
        Element elemento = padre.getOwnerDocument().createElement(nombre);
        elemento.setTextContent(valor == null ? "" : valor);
        padre.appendChild(elemento);
    }

    private void agregarOpcional(Element padre, String nombre, String valor) {
        Element elemento = padre.getOwnerDocument().createElement(nombre);
        if (valor == null || valor.isBlank()) {
            elemento.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi:nil", "true");
        } else {
            elemento.setTextContent(valor);
        }
        padre.appendChild(elemento);
    }

    private String decimal(BigDecimal valor) {
        return valor == null
                ? null
                : valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private Path resolverXsd() {
        Path inicio = Paths.get("").toAbsolutePath().normalize();
        for (Path base = inicio; base != null; base = base.getParent()) {
            Path candidato = base.resolve(XSD_RELATIVO);
            if (Files.isRegularFile(candidato)) {
                return candidato;
            }
        }
        Path candidatoDirecto = inicio.resolve("../" + XSD_RELATIVO).normalize();
        if (Files.isRegularFile(candidatoDirecto)) {
            return candidatoDirecto;
        }
        throw new XmlFacturaInvalidoExcepcion("No se encontró el esquema " + XSD_RELATIVO + ".");
    }
}
