package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.CampoPersistente;
import ar.edu.unq.epersgeist.RestauradorGenericoSql;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Cementerio;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
public class PruebaTest {

    @Autowired
    private RestauradorGenericoSql restauradorSqlGenerico;

    @Test
    void prueba() {
        List<CampoPersistente> campos =
                restauradorSqlGenerico.analizarEntidad(Ubicacion.class);

        campos.forEach(cp -> {
            System.out.println(
                    cp.getField().getName() +
                            " -> " +
                            cp.getNombreColumna() +
                            " | id=" + cp.isEsId() +
                            " | fk=" + cp.isEsManyToOne() +
                            " | disc=" + cp.isEsDiscriminador()
            );
        });
    }

    @Test
    void prueba2() {
        assertEquals("ubicacion", restauradorSqlGenerico.obtenerNombreTabla(Santuario.class));
        assertEquals("ubicacion", restauradorSqlGenerico.obtenerNombreTabla(Cementerio.class));
        assertEquals("espiritu", restauradorSqlGenerico.obtenerNombreTabla(Angelical.class));
        assertEquals("espiritu", restauradorSqlGenerico.obtenerNombreTabla(Demonio.class));
        assertEquals("medium", restauradorSqlGenerico.obtenerNombreTabla(Medium.class));
    }

    @Test
    void prueba3() {
        String sql = restauradorSqlGenerico.construirInsert(Medium.class);

        System.out.println(sql);
    }
}
