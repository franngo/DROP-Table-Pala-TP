package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorAlCrearEspirituException extends RuntimeException {
    public ErrorAlCrearEspirituException() {
        super("No se pudo crear el espiritu");
    }
}
