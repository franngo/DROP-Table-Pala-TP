package ar.edu.unq.epersgeist.controller.mappers;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.request.EspirituCreateDTO;
import ar.edu.unq.epersgeist.controller.dtos.espiritus.request.EspirituUpdateDTO;
import ar.edu.unq.epersgeist.controller.dtos.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;

public class MapperEspiritu {

    //    public static Espiritu crearDemonio(DemonioCreateDTO dto, Ubicacion ubicacion) {
//        return new Demonio(dto.nombre(), ubicacion);
//    }
//
//    public static Espiritu crearAngelical(AngelicalCreateDTO dto, Ubicacion ubicacion) {
//        return new Angelical(dto.nombre(), ubicacion);
//    }
    public static Espiritu crearEspiritu(EspirituCreateDTO dto, Ubicacion ubicacion) {
        return dto.tipoEspiritu().crear(dto.nombre(), ubicacion);
    }


    public static EspirituResponseDTO toDetalle(Espiritu espirituP) {
        return new EspirituResponseDTO(
                espirituP.getId(),
                espirituP.getNombre(),
                MapperMedium.toDetalle(espirituP.getMediumCon()),
                MapperUbicacion.toModelo(espirituP.getUbicacion()),
                espirituP.getNivelDeConexion()
        );
    }

    public static void actualizarEspiritu(Espiritu espiritu, EspirituUpdateDTO dto) {
        espiritu.setNombre(dto.nombre());
        espiritu.setNivelDeConexion(dto.nivelConexion());
    }

//    public static void actualizarAngelical(Angelical angel, AngelicalUpdateDTO dto) {
//        angel.setNombre(dto.nombre());
//        angel.setNivelDeConexion(dto.nivelConexion());
//    }

}
