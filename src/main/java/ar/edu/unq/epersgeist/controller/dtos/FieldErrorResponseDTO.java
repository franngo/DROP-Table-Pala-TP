package ar.edu.unq.epersgeist.controller.dtos;

public record FieldErrorResponseDTO(
        String field,
        String message
) {
}
