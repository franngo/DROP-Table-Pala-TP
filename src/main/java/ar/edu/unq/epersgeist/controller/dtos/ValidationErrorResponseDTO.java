package ar.edu.unq.epersgeist.controller.dtos;

import java.util.List;

public record ValidationErrorResponseDTO(
        int status,
        String code,
        List<FieldErrorResponseDTO> errors
) {
}
