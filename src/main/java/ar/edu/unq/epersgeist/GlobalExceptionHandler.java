package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.controller.dtos.ErrorMessageDTO;
import ar.edu.unq.epersgeist.controller.dtos.ErrorResponseDTO;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO validationHandler(
            MethodArgumentNotValidException e
    ) {

        var errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new ErrorMessageDTO(
                        err.getField() + ": " + err.getDefaultMessage()
                ))
                .toList();

        return new ErrorResponseDTO(
                400,
                "ARGUMENTO_INVÁLIDO",
                errors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO jsonInvalidoHandler(
            HttpMessageNotReadableException e
    ) {

        return new ErrorResponseDTO(
                400,
                "ARGUMENTO_INVÁLIDO",
                List.of(
                        new ErrorMessageDTO(
                                "El body enviado contiene valores inválidos"
                        )
                )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO illegalArgumentHandler(
            IllegalArgumentException e
    ) {

        return new ErrorResponseDTO(
                400,
                "ARGUMENTO_INVÁLIDO",
                List.of(
                        new ErrorMessageDTO(e.getMessage())
                )
        );
    }

    @ExceptionHandler({
            UbicacionInvalidaException.class,
            CoordenadasInsuficientesException.class,
            ErrorAlCrearEspirituException.class,
            ErrorAlActualizarEspirituSinIdException.class,
            ErrorAlCrearMediumException.class,
            ErrorAlActualizarMediumSinIdException.class,
            ErrorCantidadPorPaginaInvalidaExcepcion.class,
            ErrorNumeroPaginaInvalidoExcepcion.class,
            ErrorDireccionNulaException.class,
            ErrorManaMayorQueManaMaxAlCrearMedium.class,
            CampoInvalidoTipoEspirituException.class,
            CampoTipoUbicacionInvalido.class

    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO badRequestHandler(
            EpergeistException e
    ) {

        return new ErrorResponseDTO(
                400,
                "ARGUMENTO_INVÁLIDO",
                List.of(
                        new ErrorMessageDTO(e.getMessage())
                )
        );
    }

    // --------------------------------------------------------------------------------
    // 404 - NOT_FOUND
    // --------------------------------------------------------------------------------

    @ExceptionHandler({
            UbicacionNoEncontradaException.class,
            ErrorAlActualizarEspirituException.class,
            ErrorAlActualizarMediumException.class,
            ErrorDeEspirituNoEncontradoException.class,
            ErrorDeMediumNoEncontradoException.class,
            NoExisteSantuarioCorruptoException.class,
            UbicacionesNoConectadasException.class

    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO notFoundHandler(
            EpergeistException e
    ) {

        return new ErrorResponseDTO(
                404,
                "RECURSO_NO_ENCONTRADO",
                List.of(
                        new ErrorMessageDTO(e.getMessage())
                )
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
            UbicacionLejanaException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO conflictHandler(
            EpergeistException e
    ) {

        return new ErrorResponseDTO(
                409,
                "CONFLICTO_CON_ESTADO_ACTUAL",
                List.of(
                        new ErrorMessageDTO(e.getMessage())
                )
        );
    }

    // --------------------------------------------------------------------------------
}