package ar.edu.unq.epersgeist;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Field;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.reflect.Modifier;

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

    /**
     * Analiza una clase de entidad JPA utilizando reflection y extrae
     * todos los campos persistentes relevantes para la operación de restauración SQL.
     *
     * Este method construye una representación intermedia (CampoPersistente) que
     * abstrae la metadata de JPA necesaria para generar dinámicamente sentencias INSERT.
     *
     * Se incluyen:
     * - Campos simples (@Column, @Id)
     * - Relaciones @ManyToOne (como claves foráneas por ID)
     * - Discriminadores de herencia SINGLE_TABLE (@DiscriminatorValue)
     *
     * Se excluyen:
     * - @OneToMany y @ManyToMany (no se representan como columnas)
     * - Campos transient o estáticos
     *
     * Además, se consideran campos heredados desde clases base (ej. Auditable).
     *
     * @param tipo clase de entidad JPA concreta o base
     * @return lista de campos persistentes mapeados para inserción SQL
     */
    public List<CampoPersistente> analizarEntidad(Class<?> tipo) {

        List<CampoPersistente> campos = new ArrayList<>();

        // Incluye campos heredados (ej: Auditable)
        Class<?> claseActual = tipo;
        while (claseActual != null && claseActual != Object.class) {

            for (Field field : claseActual.getDeclaredFields()) {
                field.setAccessible(true);

                // Ignorar static o transient
                if (Modifier.isStatic(field.getModifiers()) ||
                        Modifier.isTransient(field.getModifiers())) {
                    continue;
                }

                // Ignorar colecciones (OneToMany, ManyToMany)
                if (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class)) {
                    continue;
                }

                CampoPersistente cp = new CampoPersistente();
                cp.setField(field);

                // =========================
                // ID
                // =========================
                if (field.isAnnotationPresent(Id.class)) {
                    cp.setEsId(true);
                    cp.setNombreColumna("id");
                    campos.add(cp);
                    continue;
                }

                // =========================
                // ManyToOne -> FK
                // =========================
                if (field.isAnnotationPresent(ManyToOne.class)) {
                    cp.setEsManyToOne(true);

                    JoinColumn join = field.getAnnotation(JoinColumn.class);
                    if (join != null && !join.name().isEmpty()) {
                        cp.setNombreColumna(join.name());
                    } else {
                        cp.setNombreColumna(field.getName() + "_id");
                    }

                    campos.add(cp);
                    continue;
                }

                // =========================
                // Column normal
                // =========================
                Column column = field.getAnnotation(Column.class);
                if (column != null && !column.name().isEmpty()) {
                    cp.setNombreColumna(column.name());
                } else {
                    cp.setNombreColumna(field.getName());
                }

                campos.add(cp);
            }

            claseActual = claseActual.getSuperclass();
        }

        // =========================
        // Campo virtual: DiscriminatorValue
        // =========================

        Class<?> claseBase = tipo;

        // Buscamos la raíz de la jerarquía que declara la herencia
        while (claseBase.getSuperclass() != null &&
                !claseBase.isAnnotationPresent(Inheritance.class)) {
            claseBase = claseBase.getSuperclass();
        }

        DiscriminatorColumn discriminatorColumn =
                claseBase.getAnnotation(DiscriminatorColumn.class);

        DiscriminatorValue discriminatorValue =
                tipo.getAnnotation(DiscriminatorValue.class);

        if (discriminatorColumn != null && discriminatorValue != null) {

            CampoPersistente cp = new CampoPersistente();

            cp.setEsDiscriminador(true);
            cp.setNombreColumna(discriminatorColumn.name());
            cp.setValorConstante(discriminatorValue.value());

            campos.add(cp);
        }

        return campos;
    }
}
