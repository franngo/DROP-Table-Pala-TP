package ar.edu.unq.epersgeist.modelo.exceptions;

public class EpergeistException extends RuntimeException {
    public EpergeistException(String message) { super(message); }
    public EpergeistException(Throwable excepcion) { super(excepcion); }
}
