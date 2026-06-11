package ar.edu.unq.epersgeist.modelo.exceptions;

public class EspirituDominadoException extends RuntimeException {
    public EspirituDominadoException() {
        super("El espiritu esta siendo dominado por otro espiritu");
    }
}
