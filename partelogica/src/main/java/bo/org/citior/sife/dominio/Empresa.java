package bo.org.citior.sife.dominio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    private Long id;
    private String nit;
    private String razonsocial;
    private String codigoSistema;
    private String modalidad;
    private String ambiente;
    private String rutaCodigo;
    private String rutaSincronizacion;
    private String rutaOperacion;
    private String rutaCompraventa;
    private String token;
    private String rutaFirmaDigital;
    private String claveFirmaDigital;
}
