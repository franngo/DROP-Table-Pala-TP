package ar.edu.unq.epersgeist.controller.dtos.espiritus.response;

import ar.edu.unq.epersgeist.controller.dtos.mediums.response.MediumDTO;
import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.response.UbicacionResponseDTO;

public record EspirituResponseDTO(
        Long id,
        String nombre,
        MediumDTO dtoM,
        UbicacionResponseDTO dtoU,
        int nivelConexion
) {
}


