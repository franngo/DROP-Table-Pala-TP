package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorDominacionInvalidaCandidatoLoDominaException extends EpergeistException {
    public ErrorDominacionInvalidaCandidatoLoDominaException(Long espirituDominanteId, Long espirituADominarId) {
        super("El espíritu de id " + espirituDominanteId + " no puede dominar al de id " + espirituADominarId + " porque "
                + "este último domina al primero.");
    }
}
