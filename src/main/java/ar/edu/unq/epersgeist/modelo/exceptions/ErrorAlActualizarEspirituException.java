package ar.edu.unq.epersgeist.modelo.exceptions;

public class ErrorAlActualizarEspirituException extends RuntimeException {
    public ErrorAlActualizarEspirituException() {
        super("No se pudo actualizar el espíritu");
    }
}
