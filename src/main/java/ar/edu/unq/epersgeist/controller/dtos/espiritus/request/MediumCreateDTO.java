package ar.edu.unq.epersgeist.controller.dtos.espiritus.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MediumCreateDTO(
        @NotBlank(message = "El nombre del medium es obligatorio")
        String nombre,
        @Min(value = 0, message = "El manaMax no puede ser menor a 0")
        int manaMax,
        @Min(value = 0, message = "El mana no puede ser menor a 0")
        //@Max(value = manaMax, message = "El mana no puede ser mayor que el manaMax")
        int mana,
        @NotNull(message = "El id de la ubicación del medium es obligatorio")
        Long ubicacionId
) {
}
