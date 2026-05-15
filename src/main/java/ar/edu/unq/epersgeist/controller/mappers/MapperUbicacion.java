package ar.edu.unq.epersgeist.controller.mappers;

import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.UbicacionCreateDTO;
import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.UbicacionUpdateDTO;
import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.response.UbicacionResponseDTO;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;

import static ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.TipoUbicacion.CEMENTERIO;
import static ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.TipoUbicacion.SANTUARIO;

public class MapperUbicacion {
    public static UbicacionResponseDTO toDTO(Ubicacion ubicacion) {
        return new UbicacionResponseDTO(
                ubicacion.getId(),
                ubicacion.getNombre(),
                ubicacion.getEnergia()
        );
    }

    public static Ubicacion toModelo(UbicacionCreateDTO dto) {
        return switch (dto.tipoUbicacion()) {
            case CEMENTERIO -> CEMENTERIO.crear(dto.nombre(), dto.energia());
            case SANTUARIO -> SANTUARIO.crear(dto.nombre(), dto.energia());
            default -> throw new IllegalArgumentException("Tipo de ubicación inválido");
        };
    }

    public static void actualizar(Ubicacion ubicacion, UbicacionUpdateDTO dto) {
        ubicacion.setNombre(dto.nombre());
        ubicacion.setEnergia(dto.energia());
    }
}
