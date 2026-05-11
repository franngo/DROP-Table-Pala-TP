package ar.edu.unq.epersgeist.controller.mappers;

import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.UbicacionCreateDTO;
import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.UbicacionUpdateDTO;
import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.response.UbicacionResponseDTO;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Cementerio;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;

public class MapperUbicacion {
    public static UbicacionResponseDTO toDTO(Ubicacion ubicacion) {
        return new UbicacionResponseDTO(ubicacion.getId(), ubicacion.getNombre());
    }

    public static Ubicacion toModelo(UbicacionCreateDTO dto) {
        return switch (dto.tipo().toUpperCase()) {
            case "CEMENTERIO" -> new Cementerio(dto.nombre());
            case "SANTUARIO" -> new Santuario(dto.nombre());
            default -> throw new IllegalArgumentException("Tipo de ubicación inválido");
        };
    }

    public static void actualizar(Ubicacion ubicacion, UbicacionUpdateDTO dto) {
        ubicacion.setNombre(dto.nombre());
    }
}
