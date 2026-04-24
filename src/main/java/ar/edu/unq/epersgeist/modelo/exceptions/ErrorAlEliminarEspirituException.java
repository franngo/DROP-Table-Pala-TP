package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorAlEliminarEspirituException extends EpergeistException {
    public ErrorAlEliminarEspirituException(Long id) { super("No se pudo eliminar el espíritu con id " + id + "."); }
}
