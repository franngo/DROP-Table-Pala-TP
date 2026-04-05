package ar.edu.unq.epersgeist.persistencia.dao.jdbc;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public record JDBCEspirituDAO() implements EspirituDAO {


    public Espiritu crear(Espiritu espiritu) {
        return JDBCConnector.getInstance().execute(conn -> {
            try (PreparedStatement ps = prepareInsertQueryStatement(espiritu, conn);
                 ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new RuntimeException("No se pudo crear el espiritu");
                }

                return buildEspiritu(rs);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static PreparedStatement prepareInsertQueryStatement(Espiritu espiritu, Connection conn) throws SQLException {

        var ps = conn.prepareStatement(
                "INSERT INTO espiritu (tipo, nombre, nivelDeConexion)" +
                        "VALUES (?,?,?) RETURNING id, tipo, nombre, nivelDeConexion"

        );

        ps.setString(1, espiritu.getTipo());
        ps.setString(2, espiritu.getNombre());
        ps.setInt(3, espiritu.getNivelDeConexion());

        return ps;
    }


    public Espiritu recuperar(Long idDelEspiritu) {
        return JDBCConnector.getInstance().execute(conn -> {
            try (PreparedStatement ps = prepareSelectQueryStatement(idDelEspiritu, conn);
                 ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new RuntimeException("No se encontró el espíritu con id " + idDelEspiritu);
                }

                return buildEspiritu(rs);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private static PreparedStatement prepareSelectQueryStatement(Long idDelEspiritu, Connection conn) throws SQLException {
        var ps = conn.prepareStatement(
                "SELECT id, tipo, nivelDeConexion, nombre FROM espiritu WHERE id = ?"
        );

        ps.setLong(1, idDelEspiritu);

        return ps;
    }


    public List<Espiritu> recuperarTodos() {

        return JDBCConnector.getInstance().execute(conn -> {

            List<Espiritu> todosLosEspiritus = new ArrayList<>();
            try (PreparedStatement ps = prepareGetAllQueryStatement(conn);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    todosLosEspiritus.add(buildEspiritu(rs));
                }

                return todosLosEspiritus;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private PreparedStatement prepareGetAllQueryStatement(Connection conn) throws SQLException {

        return conn.prepareStatement(
                "SELECT * FROM espiritu ORDER BY nombre ASC"
        );
    }

    
    @Override
    public void actualizar(Espiritu espiritu) {
        JDBCConnector.getInstance().execute(conn -> {
            try (PreparedStatement ps = prepareUpdateQueryStatement(espiritu, conn)) {

                if (espiritu.getId() == null) {
                    throw new RuntimeException("El espiritu no tiene id");
                }

                int filas = ps.executeUpdate();

                if (filas == 0) {
                    throw new RuntimeException("No se pudo actualizar el espiritu");
                }

                return null;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private PreparedStatement prepareUpdateQueryStatement(Espiritu espiritu, Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE espiritu SET tipo=?,nombre=?,nivelDeConexion=? WHERE id =  ? ");

        ps.setString(1, espiritu.getTipo());
        ps.setString(2, espiritu.getNombre());
        ps.setInt(3, espiritu.getNivelDeConexion());
        ps.setLong(4, espiritu.getId());
        return ps;
    }


    public void eliminar(Long espirituId) {
        JDBCConnector.getInstance().execute(conn -> {
            try {
                var ps = prepareDeleteQueryStatement(espirituId, conn);
                return ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static PreparedStatement prepareDeleteQueryStatement(Long espirituId, Connection conn) throws SQLException {
        var ps = conn.prepareStatement("DELETE FROM espiritu WHERE id =  ? ");
        ps.setLong(1, espirituId);
        return ps;
    }

    // Método inicializador del JDBCDAO -> ejecución de DDL. No modificar.
    @Override
    public void eliminarTodos() {
        JDBCConnector.getInstance().execute(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM espiritu")) {
                ps.executeUpdate();
                return null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

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

    /*
     *
     *   @param tiene que recibir un resulSet que no sea nulo
     * */
    private static Espiritu buildEspiritu(ResultSet resultSet) throws SQLException {

        Espiritu espiritu = new Espiritu(
                resultSet.getString("tipo"),
                resultSet.getString("nombre")
        );

        espiritu.setId(resultSet.getLong("id"));

        return espiritu;
    }

}
