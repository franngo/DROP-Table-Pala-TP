package ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UbicacionUpdateDTO(
        @NotBlank String nombre,
        @NotNull @Min(0) @Max(100) Integer energia
) {
}
