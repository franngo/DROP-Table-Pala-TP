package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

import ar.edu.unq.epersgeist.modelo.exceptions.EpergeistException;

public class UbicacionNoEncontradaException extends EpergeistException {
    public UbicacionNoEncontradaException(Long id) {
        super("La ubicación con id " + id + " no existe");
    }
}
