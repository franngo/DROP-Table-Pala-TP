package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorAlRecuperarMediumException extends RuntimeException {
    public ErrorAlRecuperarMediumException(Long id) {
        super("No se pudo recuperar al medium con id " + id + ".");
    }
}
