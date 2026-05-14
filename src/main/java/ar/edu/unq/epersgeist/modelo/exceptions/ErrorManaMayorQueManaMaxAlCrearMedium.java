package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorManaMayorQueManaMaxAlCrearMedium extends EpergeistException {
    public ErrorManaMayorQueManaMaxAlCrearMedium() {
        super("No se puede crear un medium con un valor de maná mayor a su valor de manaMax");
    }
}
