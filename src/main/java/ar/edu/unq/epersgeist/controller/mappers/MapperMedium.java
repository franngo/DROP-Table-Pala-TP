package ar.edu.unq.epersgeist.controller.mappers;

import ar.edu.unq.epersgeist.controller.dtos.mediums.request.MediumCreateDTO;
import ar.edu.unq.epersgeist.controller.dtos.mediums.request.MediumUpdateDTO;
import ar.edu.unq.epersgeist.controller.dtos.mediums.response.MediumResponseDTO;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;

public class MapperMedium {
    public static MediumResponseDTO toDTO(Medium m) {
        return new MediumResponseDTO(
                m.getId(),
                m.getNombre(),
                m.getManaMax(),
                m.getMana()

        );
    }

    public static Medium toModelo(MediumCreateDTO dtoM, Ubicacion u) {
        return new Medium(
                dtoM.nombre(),
                dtoM.manaMax(),
                dtoM.mana(),
                u
        );
    }


    public static void actualizar(Medium m, MediumUpdateDTO dtoU) {
        m.setNombre(dtoU.nombre());
        m.setManaMax(dtoU.manaMax());
        m.setMana(dtoU.mana());
    }
}


