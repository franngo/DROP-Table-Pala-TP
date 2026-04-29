package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

import ar.edu.unq.epersgeist.modelo.exceptions.EpergeistException;

public class ErrorDeUbicacionNoEncontradaException extends EpergeistException {
    public ErrorDeUbicacionNoEncontradaException(Long id) {
        super("No se pudo recuperar a la ubicación con id " + id + ".");
    }
}
