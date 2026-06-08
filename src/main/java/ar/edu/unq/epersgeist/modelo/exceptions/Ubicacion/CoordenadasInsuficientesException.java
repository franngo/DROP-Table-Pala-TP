package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

import ar.edu.unq.epersgeist.modelo.exceptions.EpergeistException;

public class CoordenadasInsuficientesException extends EpergeistException {
    public CoordenadasInsuficientesException(){
        super("Una ubicación necesita al menos 3 coordenadas para formar un área");
    }
}
