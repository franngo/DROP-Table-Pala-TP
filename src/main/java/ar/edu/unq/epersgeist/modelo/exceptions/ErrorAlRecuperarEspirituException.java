package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorAlRecuperarEspirituException extends RuntimeException {
    public ErrorAlRecuperarEspirituException(Long id) { super("No se pudo recuperar al espíritu con id " + id); }
}
