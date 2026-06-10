package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

import ar.edu.unq.epersgeist.modelo.exceptions.EpergeistException;

public class NoTodasLasUbicacionesExistenException extends EpergeistException {
    public NoTodasLasUbicacionesExistenException() {
        super("No todas las ubicaciones dadas existen.");
    }
}
