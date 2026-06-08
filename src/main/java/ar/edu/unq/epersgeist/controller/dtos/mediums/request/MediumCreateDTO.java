package ar.edu.unq.epersgeist.controller.dtos.mediums.request;

import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.CoordenadaDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MediumCreateDTO(
        @NotBlank(message = "El nombre del medium es obligatorio")
        String nombre,
        @NotNull(message = "El maná máximo del medium es obligatorio") @Min(value = 0, message = "El manaMax no puede ser menor a 0")
        Integer manaMax,
        @NotNull(message = "El maná del medium es obligatorio") @Min(value = 0, message = "El mana no puede ser menor a 0")
        Integer mana,
        @NotNull(message = "El id de la ubicación del medium es obligatorio")
        Long ubicacionId,

        @Valid
        @NotNull
        CoordenadaDTO coordenada
) {
}
