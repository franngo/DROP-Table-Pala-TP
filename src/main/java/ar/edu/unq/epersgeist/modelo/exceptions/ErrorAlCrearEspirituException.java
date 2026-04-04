package ar.edu.unq.epersgeist.modelo.exceptions;

import java.sql.SQLException;

public class ErrorAlCrearEspirituException extends RuntimeException {
    public ErrorAlCrearEspirituException(String s, SQLException e) {
    }
}
