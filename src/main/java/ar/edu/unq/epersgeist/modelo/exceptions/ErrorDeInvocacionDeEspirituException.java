package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorDeInvocacionDeEspirituException extends RuntimeException {
    public ErrorDeInvocacionDeEspirituException() {
        super("El espiritu dado no está libre, por lo que no puede ser invocado por el medium dado.");
    }
}
