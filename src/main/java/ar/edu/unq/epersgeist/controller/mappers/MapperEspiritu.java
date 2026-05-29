package ar.edu.unq.epersgeist.controller.mappers;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.request.EspirituCreateDTO;
import ar.edu.unq.epersgeist.controller.dtos.espiritus.request.EspirituUpdateDTO;
import ar.edu.unq.epersgeist.controller.dtos.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.CampoInvalidoTipoEspirituException;

import static ar.edu.unq.epersgeist.controller.dtos.espiritus.request.TipoEspiritu.ANGELICAL;
import static ar.edu.unq.epersgeist.controller.dtos.espiritus.request.TipoEspiritu.DEMONIO;

public class MapperEspiritu {


    public static Espiritu toModelo(EspirituCreateDTO dto, Ubicacion ubicacion) {

        return switch (dto.tipoEspiritu()) {
            case DEMONIO -> DEMONIO.crear(dto.nombre(), ubicacion);
            case ANGELICAL -> ANGELICAL.crear(dto.nombre(), ubicacion);
            default -> throw new CampoInvalidoTipoEspirituException("Tipo de Espiritu inválido");
        };
    }


    public static EspirituResponseDTO toDTO(Espiritu espirituP) {
        return new EspirituResponseDTO(
                espirituP.getId(),
                espirituP.getNombre(),
                espirituP.getMediumCon() == null ? null : MapperMedium.toDTO(espirituP.getMediumCon()),
                MapperUbicacion.toDTO(espirituP.getUbicacion()),
                espirituP.getNivelDeConexion(),
                espirituP.getTipoEspiritu()
        );
    }

    public static void actualizarEspiritu(Espiritu espiritu, EspirituUpdateDTO dto) {
        espiritu.setNombre(dto.nombre());
        espiritu.setNivelDeConexion(dto.nivelConexion());
    }


}
