package ar.edu.unq.epersgeist.persistencia.dao.jdbc;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public record JDBCEspirituDAO() implements EspirituDAO {

    public Espiritu crear(Espiritu espiritu) {
        // TODO completar
        return JDBCConnector.getInstance().execute(conn -> {
            try {
                var ps = prepareInsertQueryStatement(espiritu, conn);
                var resultSet = ps.executeQuery();
                return buildEspiritu(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private static PreparedStatement prepareInsertQueryStatement(Espiritu espiritu, Connection conn) throws SQLException {

        var ps = conn.prepareStatement(
                "INSERT INTO espiritu (tipo, nombre, nivelDeConexion)VALUES (?,?,?) RETURNING id, tipo, nombre, nivelDeConexion"
        );


        ps.setString(1, espiritu.getTipo());
        ps.setString(2, espiritu.getNombre());
        ps.setInt(3, espiritu.getNivelDeConexion());

        return ps;
    }

    private static Espiritu buildEspiritu(ResultSet resultSet) throws SQLException {
        Espiritu espirituModelo = null;

        while (resultSet.next()) {
            //si personaje no es null aca significa que el while dio mas de una vuelta, eso
            //suele pasar cuando el resultado (resultset) tiene mas de un elemento.

            if (espirituModelo != null) {
                //TODO preguntar si agregar errores custom
                throw new RuntimeException(String.format("Existe mas de un espiritu con el mismo id %s", espirituModelo.getId()));
            }


            espirituModelo = new Espiritu(resultSet.getLong("id"),
                    resultSet.getString("tipo"),
                    resultSet.getInt("nivelDeConexion"),
                    resultSet.getString("nombre")
            );

        }
        return espirituModelo;
    }

    public Espiritu recuperar(Long idDelEspiritu) {
        return JDBCConnector.getInstance().execute(conn -> {
            try {
                var ps = prepareSelectQueryStatement(idDelEspiritu, conn);
                var resultSet = ps.executeQuery();
                return buildEspiritu(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private static PreparedStatement prepareSelectQueryStatement(Long idDelEspiritu, Connection conn) throws SQLException {
        var ps = conn.prepareStatement("SELECT id, tipo,nivelDeConexion, nombre FROM espiritu WHERE id = ?");
        ps.setLong(1, idDelEspiritu);
        return ps;
    }

    public List<Espiritu> recuperarTodos() {
        // TODO completar
        return null;
    }

    public void actualizar(Espiritu espiritu) {
        // TODO completar
    }

    public void eliminar(Long idDelEspiritu) {
        // TODO completar
    }

    // Método inicializador del JDBCDAO -> ejecución de DDL. No modificar.
    public JDBCEspirituDAO() {
        try {
            var uri = getClass().getClassLoader().getResource("createAll.sql").toURI();
            var initializeScript = Files.readString(Paths.get(uri));
            JDBCConnector.getInstance().execute(conn -> {
                try {
                    var ps = conn.prepareStatement(initializeScript);
                    return ps.execute();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}