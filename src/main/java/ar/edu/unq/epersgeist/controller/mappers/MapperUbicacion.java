package ar.edu.unq.epersgeist.controller.mappers;

import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.response.UbicacionResponseDTO;
import ar.edu.unq.epersgeist.modelo.Ubicacion;

public class MapperUbicacion {
    public static UbicacionResponseDTO toDTO(Ubicacion ubicacion) {
        return new UbicacionResponseDTO(
                ubicacion.getId(),
                ubicacion.getNombre()
        );
    }
}
