package ar.edu.unq.epersgeist.modelo.exceptions;

public class IdEspirituInvalidoException extends EpergeistException {
    public IdEspirituInvalidoException() {
        super("El ID del espíritu no puede ser nulo.");
    }
}
