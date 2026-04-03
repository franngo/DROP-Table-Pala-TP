package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.jdbc.JDBCEspirituDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JDBCEspirituDAOTest {
    private EspirituDAO espirituDao = new JDBCEspirituDAO();
    private Espiritu espirituTest;

    @BeforeEach
    void setUp() {
        espirituTest = new Espiritu("Pokemon", 0, "das");
    }

    @Test
    void seCreaElEspirituEnLaBase() {
        var unEspiritu = espirituDao.crear(espirituTest);

        assertNotNull(unEspiritu.getId());


    }

    @Test
    void seRecuperaUnEspirituDadoUnId() {
        //creo un espiritu
        var unEspiritu = espirituDao.crear(espirituTest);
        Espiritu espirituRecuperado = espirituDao.recuperar((long) 1);

        assertEquals(1, espirituRecuperado.getId());
    }

    @AfterEach
    void tearDown() {
        // TODO implementar eliminar
        espirituDao.eliminar((long) 1);
    }
}
