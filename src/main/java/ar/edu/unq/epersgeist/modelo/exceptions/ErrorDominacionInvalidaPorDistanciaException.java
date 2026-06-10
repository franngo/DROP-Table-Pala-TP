package ar.edu.unq.epersgeist.modelo.exceptions;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;

public class ErrorDominacionInvalidaPorDistanciaException extends EpergeistException {
    public ErrorDominacionInvalidaPorDistanciaException(Long espirituDominanteId, Long espirituADominarId, double distanciaEspiritus) {
        super("El espíritu de id " + espirituDominanteId + " no puede dominar al de id " + espirituADominarId + " porque la distancia es de "
        + distanciaEspiritus + " y no cumple las reglas de la dominación.");
    }
}
