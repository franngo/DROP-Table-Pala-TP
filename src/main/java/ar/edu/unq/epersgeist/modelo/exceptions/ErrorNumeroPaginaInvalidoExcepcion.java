package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorNumeroPaginaInvalidoExcepcion extends EpergeistException {
    public ErrorNumeroPaginaInvalidoExcepcion() {
        super("El número de página debe ser mayor o igual a 1.");
    }
}
