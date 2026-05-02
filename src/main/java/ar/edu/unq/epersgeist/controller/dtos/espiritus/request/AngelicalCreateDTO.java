package ar.edu.unq.epersgeist.controller.dtos.espiritus.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AngelicalCreateDTO(
        @NotBlank(message = "El nombre del angel es obligatorio")
        String nombre,
        @NotNull(message = "El id de la ubicacion del angel es obligatoria")
        Long ubicacionId

) {
}