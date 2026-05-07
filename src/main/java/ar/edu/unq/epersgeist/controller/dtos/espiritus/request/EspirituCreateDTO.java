package ar.edu.unq.epersgeist.controller.dtos.espiritus.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EspirituCreateDTO(
        @NotBlank(message = "El nombre del demonio es obligatorio")
        String nombre,
        @NotNull(message = "El id de la ubicacion del demonio es obligatoria")
        Long ubicacionId,
        @NotNull(message = "El tipo indicado no es correcto")
        TipoEspiritu tipoEspiritu

) {
}



