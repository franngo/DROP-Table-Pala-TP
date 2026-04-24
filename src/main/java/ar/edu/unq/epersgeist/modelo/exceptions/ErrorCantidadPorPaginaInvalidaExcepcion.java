package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorCantidadPorPaginaInvalidaExcepcion extends EpergeistException {
    public ErrorCantidadPorPaginaInvalidaExcepcion() {
        super("La cantidad por página debe ser mayor o igual a 1.");
    }
}
