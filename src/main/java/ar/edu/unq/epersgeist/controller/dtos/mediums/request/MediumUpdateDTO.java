package ar.edu.unq.epersgeist.controller.dtos.mediums.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record MediumUpdateDTO(
        @NotBlank(message = "El nombre del medium es obligatorio")
        String nombre,
        @Min(value = 0, message = "El manaMax no puede ser menor a 0")
        int manaMax,
        @Min(value = 0, message = "El mana no puede ser menor a 0")
        //@Max(value = manaMax, message = "El mana no puede ser mayor que el manaMax")
        int mana
) {
}
