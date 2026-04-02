package ar.edu.unq.epersgeist.persistencia.dao.jdbc;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

public record JDBCEspirituDAO() implements EspirituDAO {

    public Espiritu crear(Espiritu espiritu) {
        // TODO completar
        JDBCConnector.getInstance().execute(conn  -> {
            try {
                var ps = prepareInsertQueryStatement(espiritu, conn);
               // return ps.execute();
                return this.buildEspiritu(ps.execute());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
        private static PreparedStatement prepareInsertQueryStatement(Espiritu espiritu, Connection conn) throws SQLException {
            var ps = conn.prepareStatement("INSERT INTO espiritu (tipo, nombre, nivelDeConexion) VALUES (?,?,?)");
            ps.setString(1, espiritu.getTipo());
            ps.setString(2, espiritu.getNombre());
            ps.setInt(3, espiritu.getNivelDeConexion());

            return ps;
        }
    private static Espiritu buildPersonaje(String nombre, ResultSet resultSet) throws SQLException {
        Espiritu personaje = null;
        while (resultSet.next()) {
            //si personaje no es null aca significa que el while dio mas de una vuelta, eso
            //suele pasar cuando el resultado (resultset) tiene mas de un elemento.
            if (personaje != null) {
                throw new RuntimeException(String.format("Existe mas de un personaje con el nombre %s", nombre));
            }
            personaje = new Espiritu(nombre,
                    resultSet.getInt("pesoMaximo"),
                    resultSet.getInt("xp"),
                    resultSet.getInt("vida"),
                    new HashSet<>()
            );
        }
        return personaje;
    }

    public Espiritu recuperar(Long idDelEspiritu) {
        // TODO completar
        return null;
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