package ar.edu.unq.epersgeist;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@Transactional
@AllArgsConstructor
public class RestauradorGenericoSql {

    private final JdbcTemplate jdbcTemplate;

    public <T> void restaurar(List<T> entidades) {

        if (entidades == null || entidades.isEmpty()) {
            return;
        }

        Class<?> tipo = entidades.getFirst().getClass();

        String tabla = obtenerNombreTabla(tipo);

        String sql = construirInsert(tipo);

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {

                T entidad = entidades.get(i);

                cargarParametros(ps, entidad);
            }

            @Override
            public int getBatchSize() {
                return entidades.size();
            }
        });

        sincronizarSecuencia(tabla);
    }

    private String obtenerNombreTabla(Class<?> tipo) {
        // devuelve el nombre de la tabla SQL asociada a la entidad raíz
        return null;
    }

    private String construirInsert(Class<?> tipo) {
        // construye dinámicamente el SQL INSERT según los campos persistidos
        return null;
    }

    private void cargarParametros(PreparedStatement ps, Object entidad) throws SQLException {
        // carga los valores de la entidad en el PreparedStatement
    }

    private void sincronizarSecuencia(String tabla) {
        // ajusta la sequence de PostgreSQL al MAX(id) de la tabla
    }
}
