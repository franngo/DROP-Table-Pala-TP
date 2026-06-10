package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorDominacionInvalidaCandidatoNoAptoException extends EpergeistException {
    public ErrorDominacionInvalidaCandidatoNoAptoException(Long espirituDominanteId, Long espirituADominarId) {
        super("El espíritu de id " + espirituDominanteId + " no puede dominar al de id " + espirituADominarId + " porque "
                + "este último no es apto para ser dominado por Espiritus en este momento.");
    }
}
