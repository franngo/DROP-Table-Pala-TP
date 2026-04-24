package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

public class UbicacionYaExisteException extends RuntimeException {
    public UbicacionYaExisteException(String nombre) {
        super("Ya existe una ubicación con el nombre '" + nombre + "'.");
    }
}
