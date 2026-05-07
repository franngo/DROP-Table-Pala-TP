package ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request;

import jakarta.validation.constraints.NotBlank;

public record UbicacionUpdateDTO(
        @NotBlank String nombre
) {
}
