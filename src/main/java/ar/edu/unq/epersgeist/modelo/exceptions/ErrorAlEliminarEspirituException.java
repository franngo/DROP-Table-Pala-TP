package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorAlEliminarEspirituException extends RuntimeException {
    public ErrorAlEliminarEspirituException(Long id) { super("No se pudo eliminar el espíritu con id " + id); }
}
