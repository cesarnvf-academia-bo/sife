package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.excepcion.FirmaDigitalNoConfiguradaExcepcion;
import bo.org.citior.sife.excepcion.XmlFacturaInvalidoExcepcion;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
@RequiredArgsConstructor
public class FirmarXmlServicio {

    private static final String DS_NAMESPACE = XMLSignature.XMLNS;

    private final GenerarXmlFacturaServicio generarXmlFacturaServicio;

    public byte[] firmar(Document documento, Empresa empresa) {
        if (documento == null) {
            throw new XmlFacturaInvalidoExcepcion("El documento XML a firmar es nulo.");
        }

        eliminarFirmas(documento);
        KeyStore.PrivateKeyEntry entrada = cargarEntrada(empresa);
        X509Certificate certificado = (X509Certificate) entrada.getCertificate();

        try {
            XMLSignatureFactory fabrica = XMLSignatureFactory.getInstance("DOM");
            Reference referencia = fabrica.newReference(
                    "",
                    fabrica.newDigestMethod(DigestMethod.SHA256, null),
                    List.of(
                            fabrica.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null),
                            fabrica.newTransform(
                                    CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                                    (TransformParameterSpec) null)),
                    null,
                    null);
            SignedInfo informacion = fabrica.newSignedInfo(
                    fabrica.newCanonicalizationMethod(
                            CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                    fabrica.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
                    List.of(referencia));

            KeyInfoFactory fabricaClave = fabrica.getKeyInfoFactory();
            X509Data datosCertificado = fabricaClave.newX509Data(List.of(certificado));
            KeyInfo informacionClave = fabricaClave.newKeyInfo(List.of(datosCertificado));

            XMLSignature firma = fabrica.newXMLSignature(informacion, informacionClave);
            DOMSignContext contexto = new DOMSignContext(
                    entrada.getPrivateKey(), documento.getDocumentElement());
            contexto.setDefaultNamespacePrefix("ds");
            contexto.putNamespacePrefix(DS_NAMESPACE, "ds");
            firma.sign(contexto);
        } catch (GeneralSecurityException | MarshalException | XMLSignatureException excepcion) {
            throw new XmlFacturaInvalidoExcepcion(
                    "No se pudo generar la firma digital: " + excepcion.getMessage());
        }

        byte[] xmlFirmado = serializar(documento);
        validarFirma(xmlFirmado, certificado);
        generarXmlFacturaServicio.validar(parsear(xmlFirmado));
        return xmlFirmado;
    }

    private void eliminarFirmas(Document documento) {
        NodeList firmas = documento.getElementsByTagNameNS(DS_NAMESPACE, "Signature");
        for (int indice = firmas.getLength() - 1; indice >= 0; indice--) {
            Node nodo = firmas.item(indice);
            if (nodo.getParentNode() != null) {
                nodo.getParentNode().removeChild(nodo);
            }
        }
    }

    private KeyStore.PrivateKeyEntry cargarEntrada(Empresa empresa) {
        String ruta = empresa == null ? null : empresa.getRutaFirmaDigital();
        String clave = empresa == null ? null : empresa.getClaveFirmaDigital();
        if (ruta == null || ruta.isBlank() || clave == null || clave.isBlank()) {
            throw new FirmaDigitalNoConfiguradaExcepcion(
                    "La ruta o la clave de la firma digital no están configuradas.");
        }

        try {
            Path archivo = Paths.get(ruta.trim());
            if (!Files.isRegularFile(archivo)) {
                throw new FirmaDigitalNoConfiguradaExcepcion(
                        "No se encontró el archivo de firma digital.");
            }

            char[] password = clave.trim().toCharArray();
            KeyStore almacen = KeyStore.getInstance("PKCS12");
            try (InputStream entrada = Files.newInputStream(archivo)) {
                almacen.load(entrada, password);
            }

            for (String alias : Collections.list(almacen.aliases())) {
                if (!almacen.isKeyEntry(alias)) {
                    continue;
                }
                KeyStore.Entry item = almacen.getEntry(
                        alias, new KeyStore.PasswordProtection(password));
                if (item instanceof KeyStore.PrivateKeyEntry privada
                        && privada.getPrivateKey() != null
                        && privada.getCertificate() instanceof X509Certificate certificado) {
                    return privada;
                }
            }
        } catch (GeneralSecurityException | IOException | InvalidPathException excepcion) {
            throw new FirmaDigitalNoConfiguradaExcepcion(
                    "No se pudo cargar la firma digital: " + excepcion.getMessage());
        }

        throw new FirmaDigitalNoConfiguradaExcepcion(
                "El archivo de firma digital no contiene una clave privada.");
    }

    private byte[] serializar(Document documento) {
        try {
            Transformer transformador = TransformerFactory.newInstance().newTransformer();
            transformador.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
            transformador.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformador.setOutputProperty(OutputKeys.STANDALONE, "yes");
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            transformador.transform(new DOMSource(documento), new StreamResult(salida));
            return salida.toByteArray();
        } catch (TransformerException excepcion) {
            throw new XmlFacturaInvalidoExcepcion(
                    "No se pudo serializar el XML firmado: " + excepcion.getMessage());
        }
    }

    private void validarFirma(byte[] xmlFirmado, X509Certificate certificado) {
        try {
            Document documento = parsear(xmlFirmado);
            NodeList firmas = documento.getElementsByTagNameNS(DS_NAMESPACE, "Signature");
            if (firmas.getLength() == 0) {
                throw new XmlFacturaInvalidoExcepcion("El XML firmado no contiene ds:Signature.");
            }

            DOMValidateContext contexto = new DOMValidateContext(
                    certificado.getPublicKey(), firmas.item(0));
            XMLSignatureFactory fabrica = XMLSignatureFactory.getInstance("DOM");
            XMLSignature firma = fabrica.unmarshalXMLSignature(contexto);
            if (!firma.validate(contexto)) {
                throw new XmlFacturaInvalidoExcepcion("La firma digital del XML no es válida.");
            }
        } catch (MarshalException | XMLSignatureException excepcion) {
            throw new XmlFacturaInvalidoExcepcion(
                    "No se pudo validar la firma digital: " + excepcion.getMessage());
        }
    }

    private Document parsear(byte[] xml) {
        try {
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            fabrica.setNamespaceAware(true);
            return fabrica.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
        } catch (ParserConfigurationException | SAXException | IOException excepcion) {
            throw new XmlFacturaInvalidoExcepcion(
                    "No se pudo leer el XML firmado: " + excepcion.getMessage());
        }
    }
}
