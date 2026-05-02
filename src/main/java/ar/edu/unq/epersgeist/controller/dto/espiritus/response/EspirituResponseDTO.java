package ar.edu.unq.epersgeist.controller.dto.espiritus.response;

public record EspirituResponseDTO(
        Long id,
        String nombre,
        MediumDTO dtoM,
        UbicacionDTO dtoU,
        int nivelConexion
) {
}


