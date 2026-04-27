package ar.edu.unq.epersgeist.modelo.exceptions;

public class IdMediumInvalidoException extends EpergeistException {
    public IdMediumInvalidoException() {
        super("El ID del medium no puede ser nulo.");
    }
}
