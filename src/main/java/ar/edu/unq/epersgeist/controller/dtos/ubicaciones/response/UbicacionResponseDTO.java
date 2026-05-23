package ar.edu.unq.epersgeist.controller.dtos.ubicaciones.response;

import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.TipoUbicacion;

public record UbicacionResponseDTO(
        Long id,
        String nombre,
        TipoUbicacion tipo,
        Integer energia
) {
}
