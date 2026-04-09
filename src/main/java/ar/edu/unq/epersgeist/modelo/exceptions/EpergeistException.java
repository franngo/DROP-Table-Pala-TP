package ar.edu.unq.epersgeist.modelo.exceptions;

public class EpergeistException extends RuntimeException {
    public EpergeistException(Throwable excepcion, String message) { super(message, excepcion); }
}
