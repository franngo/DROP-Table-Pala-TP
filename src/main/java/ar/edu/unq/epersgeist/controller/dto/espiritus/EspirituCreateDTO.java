package ar.edu.unq.epersgeist.controller.dto.espiritus;

import jakarta.validation.constraints.NotBlank;

public record EspirituCreateDTO(
        @NotBlank(message = "El nombre del espiritu es obligatorio")
        String nombre,
        @NotBlank(message = "La ubicacion del espiritu es obligatoria")
        Long ubicacionId
) {
}




