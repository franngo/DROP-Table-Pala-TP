package ar.edu.unq.epersgeist.modelo.exceptions;

public class EspirituMuyLejanoException extends RuntimeException {
    public EspirituMuyLejanoException() {
        super("El espíritu se encuentra a más de 50 kilómetros del médium");
    }
}
