package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

public class UbicacionInvalidaException extends RuntimeException {
    public UbicacionInvalidaException() {
        super("La ubicación no puede ser nula.");
    }
}
