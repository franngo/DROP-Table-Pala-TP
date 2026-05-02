package ar.edu.unq.epersgeist.controller.dtos.espiritus;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.modelo.Direccion;

import java.util.List;

public record PaginacionEspirituResponseDTO(
        List<EspirituResponseDTO> espiritus,
        Direccion direccion,
        int pagActual,
        int cantPagina,
        int totalPaginas
) {
}

