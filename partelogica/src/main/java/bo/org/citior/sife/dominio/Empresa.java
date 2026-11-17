package bo.org.citior.sife.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "empresa")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nit", length = 20)
    private String nit;

    @Column(name = "razonsocial", length = 250)
    private String razonsocial;

    @Column(name = "codigoSistema", length = 50)
    private String codigoSistema;

    @Column(name = "modalidad", length = 50)
    private String modalidad;

    @Column(name = "ambiente", length = 50)
    private String ambiente;

    @Column(name = "rutacodigo")
    private String rutaCodigo;

    @Column(name = "rutasincronizacion")
    private String rutaSincronizacion;

    @Column(name = "rutaoperacion")
    private String rutaOperacion;

    @Column(name = "rutacompraventa")
    private String rutaCompraventa;

    @Column(name = "token")
    private String token;

    @Column(name = "rutafirmadigital")
    private String rutaFirmaDigital;

    @Column(name = "clavefirmadigital")
    private String claveFirmaDigital;
}
