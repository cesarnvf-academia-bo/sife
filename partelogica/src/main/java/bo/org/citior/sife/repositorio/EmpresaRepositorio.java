package bo.org.citior.sife.repositorio;

import bo.org.citior.sife.dominio.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepositorio extends JpaRepository<Empresa, Long> {
}
