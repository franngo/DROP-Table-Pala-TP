package ar.edu.unq.epersgeist.controller.dtos;

import java.util.List;

public record ErrorResponseDTO(
        int status,
        String code,
        List<ErrorMessageDTO> errors
) {
}
