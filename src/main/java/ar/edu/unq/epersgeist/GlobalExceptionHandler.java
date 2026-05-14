package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.controller.dtos.ErrorMessageDTO;
import ar.edu.unq.epersgeist.controller.dtos.ErrorResponseDTO;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionInvalidaException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionYaExisteException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // --------------------------------------------------------------------------------
    // 400 - BAD_REQUEST
    // --------------------------------------------------------------------------------

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO badRequestHandler(MethodArgumentNotValidException e) {
        var errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new ErrorMessageDTO(
                        err.getField() + ": " + err.getDefaultMessage()
                ))
                .toList();


        return new ErrorResponseDTO(400, "ARGUMENTO_INVÁLIDO", errors);
    }

    @ExceptionHandler({
            UbicacionInvalidaException.class,
            ErrorAlActualizarMediumException.class,
            ErrorAlCrearEspirituException.class,
            ErrorAlCrearMediumException.class,
            ErrorCantidadPorPaginaInvalidaExcepcion.class,
            ErrorNumeroPaginaInvalidoExcepcion.class,
            ErrorDireccionNulaException.class,
            IdEspirituInvalidoException.class,
            IdMediumInvalidoException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO badRequestHandler(EpergeistException e) {
        return new ErrorResponseDTO(
                400,
                "ARGUMENTO_INVÁLIDO",
                List.of(new ErrorMessageDTO(e.getMessage()))
        );
    }

    // --------------------------------------------------------------------------------
    // 404 - NOT_FOUND
    // --------------------------------------------------------------------------------

    @ExceptionHandler({
            UbicacionNoEncontradaException.class,
            ErrorDeEspirituNoEncontradoException.class,
            ErrorDeMediumNoEncontradoException.class,
            NoExisteSantuarioCorruptoException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO notFoundHandler(EpergeistException e) {
        return new ErrorResponseDTO(
                404,
                "RECURSO_NO_ENCONTRADO",
                List.of(new ErrorMessageDTO(e.getMessage()))
        );
    }

    // --------------------------------------------------------------------------------
    // 409 - CONFLICT
    // --------------------------------------------------------------------------------

    @ExceptionHandler({
            UbicacionYaExisteException.class,
            ErrorAlConectarEspirituConMedium.class,
            ErrorDeInvocacionDeEspirituException.class,
            ErrorDeMediumExorcizandoAOtroEnDistintaUbicacion.class,
            ExorcismoPropioNoPermitidoException.class,
            ExorcistaSinAngelesException.class,
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO conflictHandler(EpergeistException e) {
        return new ErrorResponseDTO(
                409,
                "CONFLICTO_CON_ESTADO_ACTUAL",
                List.of(new ErrorMessageDTO(e.getMessage()))
        );
    }

    // --------------------------------------------------------------------------------
}
