package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

import ar.edu.unq.epersgeist.modelo.exceptions.EpergeistException;

public class CoordenadaNoPerteneceAUbicacion extends EpergeistException {
    public CoordenadaNoPerteneceAUbicacion() {
        super("La coordenada dada no pertenece al área de la Ubicacion de la entidad dada.");
    }
}
