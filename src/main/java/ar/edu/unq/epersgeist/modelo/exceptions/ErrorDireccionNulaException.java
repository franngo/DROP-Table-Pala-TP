package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorDireccionNulaException extends EpergeistException {
    public ErrorDireccionNulaException() {
        super("La dirección no debería ser nula.");
    }
}
