package ar.edu.unq.epersgeist.controller.dtos;

public record ErrorResponseDTO(
        int status,
        String code,
        String message
) {
}
