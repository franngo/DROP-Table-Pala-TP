package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorDeEspirituNoEncontradoException extends EpergeistException {
    public ErrorDeEspirituNoEncontradoException(Long id) { super("No se pudo recuperar al espíritu con id " + id + "."); }
}
