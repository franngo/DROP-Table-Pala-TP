package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

import ar.edu.unq.epersgeist.modelo.exceptions.EpergeistException;

public class InterseccionInvalidaException extends EpergeistException {
    public InterseccionInvalidaException() {
        super("La coordenada dada pertenece al área de una ubicación ya existente.");
    }
}
