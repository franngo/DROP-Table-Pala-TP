package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorAlCrearMediumException extends EpergeistException {
    public ErrorAlCrearMediumException() { super("No se pudo crear el medium en la DB de forma exitosa."); }
}
