package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

public class UbicacionNoEncontradaException extends RuntimeException {
    public UbicacionNoEncontradaException(Long id) {
        super("No se encontró una ubicación con el ID: " + id + ".");
    }
}
