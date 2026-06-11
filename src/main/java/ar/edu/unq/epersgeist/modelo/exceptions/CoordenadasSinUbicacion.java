package ar.edu.unq.epersgeist.modelo.exceptions;

public class CoordenadasSinUbicacion extends EpergeistException {
    public CoordenadasSinUbicacion(){ super("No existe ubicacion que contenga la coordenada dada");}
}
