package ar.edu.unq.epersgeist.controller.mappers;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.PaginacionEspirituResponseDTO;
import ar.edu.unq.epersgeist.modelo.Direccion;
import org.springframework.data.domain.Page;

import java.util.function.Function;


public class PageMapper {

//    public static <E, D> PaginacionEspirituResponseDTO toEspirituPage(
//            Page<E> page,
//            Function<E, D> mapper,
//            Direccion direccion
//    ) {
//        return new PaginacionEspirituResponseDTO(
//                page.map(mapper).getContent(),
//                direccion,
//                page.getNumber() + 1,   // ⚠️ importante
//                page.getSize(),
//                page.getTotalPages()
//        );
//    }
}