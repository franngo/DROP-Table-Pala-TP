package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

import ar.edu.unq.epersgeist.modelo.exceptions.EpergeistException;

public class UbicacionYaExisteException extends EpergeistException {
    public UbicacionYaExisteException(String nombre) {
        super("La ubicación '" + nombre + "' ya existe");
    }
}
