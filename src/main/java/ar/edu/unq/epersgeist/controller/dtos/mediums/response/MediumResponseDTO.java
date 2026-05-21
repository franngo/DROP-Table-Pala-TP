package ar.edu.unq.epersgeist.controller.dtos.mediums.response;

import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.response.UbicacionResponseDTO;

public record MediumResponseDTO(
        Long id,
        String nombre,
        int manaMax,
        int mana,
        UbicacionResponseDTO ubicacion
) {
}
