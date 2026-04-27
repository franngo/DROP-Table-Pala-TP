package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

public class ErrorAlCrearUbicacionException extends RuntimeException {
    public ErrorAlCrearUbicacionException(String tipo) {
      super("No se pudo crear el espíritu en la DB de forma exitosa por la siguiente razón: " + tipo);
    }
}
