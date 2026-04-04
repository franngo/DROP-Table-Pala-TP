package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.jdbc.JDBCEspirituDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JDBCEspirituDAOTest {
    private EspirituDAO espirituDao = new JDBCEspirituDAO();
    private Espiritu espirituM;

    @BeforeEach
    void setUp() {
        espirituM = new Espiritu("Pokemon", 0, "das");
    }

    @Test
    void seCreaElEspirituEnLaBase() {
        //veo que el modelo no tiene id
        assertNull(espirituM.getId());
        //exercise 
        Espiritu espirituP = espirituDao.crear(espirituM);

        //Compruebo que no son el mismo
        assertNotEquals(espirituM, espirituP);

        //Compruebo que el modelo se persistio porque tiene id
        assertNotNull(espirituP.getId());


    }

    @Test
    void seRecuperaUnEspirituDadoUnId() {
        //creo un espiritu
        Espiritu unEspiritu = espirituDao.crear(espirituM);
        Espiritu espirituRecuperado = espirituDao.recuperar((long) 1);

        assertEquals(1, espirituRecuperado.getId());
    }

    @AfterEach
    void tearDown() {
        // TODO implementar eliminar
        espirituDao.eliminar((long) 1);
    }
}
