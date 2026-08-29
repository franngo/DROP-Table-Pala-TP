package ar.edu.unq.epersgeist.controller.mappers;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.PaginacionEspirituResponseDTO;
import ar.edu.unq.epersgeist.controller.dtos.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.modelo.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import org.springframework.data.domain.Page;

import java.util.function.Function;


public class PageMapper {

    public static PaginacionEspirituResponseDTO toEspirituPage(
            Page<Espiritu> page,
            Function<Espiritu, EspirituResponseDTO> mapper,
            Direccion direccion
    ) {
        return new PaginacionEspirituResponseDTO(
                page.map(mapper).getContent(),
                direccion,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalPages()
        );
    }
}