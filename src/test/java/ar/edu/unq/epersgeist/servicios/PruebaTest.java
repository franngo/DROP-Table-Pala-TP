package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.CampoPersistente;
import ar.edu.unq.epersgeist.RestauradorGenericoSql;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
}
