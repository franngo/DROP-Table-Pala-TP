package ar.edu.unq.epersgeist.controller.mappers;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.request.EspirituCreateDTO;
import ar.edu.unq.epersgeist.controller.dtos.espiritus.request.EspirituUpdateDTO;
import ar.edu.unq.epersgeist.controller.dtos.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;

public class MapperEspiritu {


    public static Espiritu toModelo(EspirituCreateDTO dto, Ubicacion ubicacion) {
        return dto.tipoEspiritu().crear(dto.nombre(), ubicacion);
    }


    public static EspirituResponseDTO toDTO(Espiritu espirituP) {
        return new EspirituResponseDTO(
                espirituP.getId(),
                espirituP.getNombre(),
                espirituP.getMediumCon() == null ? null : MapperMedium.toDTO(espirituP.getMediumCon()),
                MapperUbicacion.toDTO(espirituP.getUbicacion()),
                espirituP.getNivelDeConexion()
        );
    }

    public static void actualizarEspiritu(Espiritu espiritu, EspirituUpdateDTO dto) {
        espiritu.setNombre(dto.nombre());
        espiritu.setNivelDeConexion(dto.nivelConexion());
    }


}
