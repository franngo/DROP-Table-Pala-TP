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
        System.out.println("ID del modelo");
        System.out.println(espirituM.getId());
        assertNull(espirituM.getId());
        Espiritu espirituP = espirituDao.crear(espirituM);
        System.out.println("ID de la base");

        System.out.println(espirituP.getId());
        //Compruebo que no son el mismo
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
