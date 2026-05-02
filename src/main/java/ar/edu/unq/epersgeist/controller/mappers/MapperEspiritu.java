package ar.edu.unq.epersgeist.controller.mappers;

import ar.edu.unq.epersgeist.controller.dto.espiritus.request.AngelicalCreateDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritus.request.DemonioCreateDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;

public class MapperEspiritu {

    public static Espiritu crearDemonio(DemonioCreateDTO dto, Ubicacion ubicacion) {
        return new Demonio(dto.nombre(), ubicacion);
    }

    public static Espiritu crearAngelical(AngelicalCreateDTO dto, Ubicacion ubicacion) {
        return new Angelical(dto.nombre(), ubicacion);
    }

    public static EspirituResponseDTO toDetalle(Espiritu espirituP) {
        return new EspirituResponseDTO(
                espirituP.getId(),
                espirituP.getNombre(),
                MapperMedium.toDetalle(espirituP.getMediumCon()),
                MapperUbicacion.toDetalle(espirituP.getUbicacion()),
                espirituP.getNivelDeConexion()
        );
    }
}
