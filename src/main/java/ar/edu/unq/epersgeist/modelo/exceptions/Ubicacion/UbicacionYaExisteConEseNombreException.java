package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

import ar.edu.unq.epersgeist.modelo.exceptions.EpergeistException;

public class UbicacionYaExisteConEseNombreException extends EpergeistException {
    public UbicacionYaExisteConEseNombreException(String nombre) {
        super("Ya existe una ubicación con el nombre '" + nombre + "'.");
    }
}
