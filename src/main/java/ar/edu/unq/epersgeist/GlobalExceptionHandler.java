package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.controller.dtos.ErrorResponseDTO;
import ar.edu.unq.epersgeist.controller.dtos.FieldErrorResponseDTO;
import ar.edu.unq.epersgeist.controller.dtos.ValidationErrorResponseDTO;
import ar.edu.unq.epersgeist.modelo.exceptions.EpergeistException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionYaExisteException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // --------------------------------------------------------------------------------
    // 400 - BAD_REQUEST
    // --------------------------------------------------------------------------------

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponseDTO badRequestHandler(MethodArgumentNotValidException e) {
        var errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map((err) -> new FieldErrorResponseDTO(err.getField(), err.getDefaultMessage()))
                .toList();

        return new ValidationErrorResponseDTO(400, "VALIDACION_ERROR", errors);
    }

    // --------------------------------------------------------------------------------
    // 404 - NOT_FOUND
    // --------------------------------------------------------------------------------

    @ExceptionHandler({
            UbicacionNoEncontradaException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO notFoundHandler(EpergeistException e) {
        return new ErrorResponseDTO(404, "RECURSO_NO_ENCONTRADO", e.getMessage());
    }

    // --------------------------------------------------------------------------------
    // 409 - CONFLICT
    // --------------------------------------------------------------------------------

    @ExceptionHandler({
            UbicacionYaExisteException.class,
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO conflictHandler(EpergeistException e) {
        return new ErrorResponseDTO(409, "RECURSO_DUPLICADO", e.getMessage());
    }

    // --------------------------------------------------------------------------------
}
