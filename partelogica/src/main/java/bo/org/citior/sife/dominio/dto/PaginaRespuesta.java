package bo.org.citior.sife.dominio.dto;

import java.util.List;

public record PaginaRespuesta<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize) {
}
