package ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record UbicacionCreateDTO(
        @NotBlank String nombre,
        @NotNull @Min(0) @Max(100) Integer energia,

        @NotNull(message = "El tipo indicado no es correcto")
        TipoUbicacion tipoUbicacion,

        @Valid
        @NotEmpty
        @Size(min=3)
        List<CoordenadaDTO> coordenadas
){

}

