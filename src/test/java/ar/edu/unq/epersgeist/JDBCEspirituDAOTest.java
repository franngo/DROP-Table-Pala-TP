package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.jdbc.JDBCEspirituDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JDBCEspirituDAOTest {
    private EspirituDAO espirituDao = new JDBCEspirituDAO();
    private Espiritu espirituTest;

    @BeforeEach
    void setUp(){
        espirituTest = new Espiritu("Pokemon",0,"das");
    }

    @Test
    void seCreaElEspirituEnLaBase(){
        var unEspiritu = espirituDao.crear(espirituTest);

        assertNotNull(unEspiritu.getId());

    }

}
