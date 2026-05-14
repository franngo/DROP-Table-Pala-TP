package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorDeMediumExorcizandoAOtroEnDistintaUbicacion extends EpergeistException {
    public ErrorDeMediumExorcizandoAOtroEnDistintaUbicacion(Long id1, Long id2) {
        super( "El medium de id " + id1 + " no puede exorcizar al medium de id " + id2 + " porque estos no se encuentra en la misma ubicación" );
    }
}
