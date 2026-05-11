package ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UbicacionCreateDTO(
        @NotBlank String nombre,
        @NotBlank @Min(0) @Max(100) Integer energia,
        @NotBlank String tipo
) {
}
