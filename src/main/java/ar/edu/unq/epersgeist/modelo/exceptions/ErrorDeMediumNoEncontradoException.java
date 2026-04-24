package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorDeMediumNoEncontradoException extends EpergeistException {
    public ErrorDeMediumNoEncontradoException(Long id) {
        super("No se pudo recuperar al medium con id " + id + ".");
    }
}
