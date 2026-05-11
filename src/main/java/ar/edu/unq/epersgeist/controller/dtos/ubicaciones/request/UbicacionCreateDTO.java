package ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request;

import jakarta.validation.constraints.NotBlank;

public record UbicacionCreateDTO(
        @NotBlank String nombre,
        @NotBlank String tipo
) {
}
