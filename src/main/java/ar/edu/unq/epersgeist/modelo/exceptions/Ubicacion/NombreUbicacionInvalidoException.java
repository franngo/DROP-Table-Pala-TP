package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

public class NombreUbicacionInvalidoException extends IllegalArgumentException {
    public NombreUbicacionInvalidoException() {
        super("El nombre de la ubicación no puede ser nulo o vacío.");
    }
}
