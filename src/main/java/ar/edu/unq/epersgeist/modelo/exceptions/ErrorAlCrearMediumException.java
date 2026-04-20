package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorAlCrearMediumException extends RuntimeException {
    public ErrorAlCrearMediumException() { super("No se pudo crear el medium en la DB de forma exitosa."); }
}
