package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

public class IdUbicacionInvalidoException extends IllegalArgumentException {
    public IdUbicacionInvalidoException() {
        super("El ID de la ubicación no puede ser nulo.");
    }
}
