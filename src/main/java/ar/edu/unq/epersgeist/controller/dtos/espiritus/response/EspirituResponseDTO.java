package ar.edu.unq.epersgeist.controller.dtos.espiritus.response;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.request.TipoEspiritu;
import ar.edu.unq.epersgeist.controller.dtos.mediums.response.MediumResponseDTO;
import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.response.UbicacionResponseDTO;

public record EspirituResponseDTO(
        Long id,
        String nombre,
        MediumResponseDTO medium,
        UbicacionResponseDTO ubicacion,
        double nivelConexion,
        TipoEspiritu tipo
) {
}


