package ar.edu.unq.epersgeist.controller.dto.espiritus.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record DemonioUpdateDTO(

        @NotBlank(message = "El nombre del demonio es obligatorio")
        String nombre,
        @Min(value = 0, message = "El nivel de conexión no puede ser menor a 0")
        @Max(value = 100, message = "El nivel de conexión no puede ser mayor a 100")
        int nivelConexion
) {
}
