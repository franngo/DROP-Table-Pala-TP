package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

import ar.edu.unq.epersgeist.modelo.exceptions.EpergeistException;

public class UbicacionInvalidaException extends EpergeistException {
    public UbicacionInvalidaException() {
        super("La ubicación no puede ser nula.");
    }
}
