package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

import ar.edu.unq.epersgeist.modelo.exceptions.EpergeistException;

public class UbicacionNoEncontradaException extends EpergeistException {
    public UbicacionNoEncontradaException(Long id) {
        super("No se encontró una ubicación con el ID: " + id + ".");
    }
}
