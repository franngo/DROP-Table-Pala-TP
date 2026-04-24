package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorAlCrearEspirituException extends EpergeistException {
    public ErrorAlCrearEspirituException() {
        super("No se pudo crear el espíritu en la DB de forma exitosa.");
    }
}
