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
    public void setUp() {
        espirituM = new Espiritu("Pokemon", 0, "das");
    }

    @Test
    public void seCreaElEspirituEnLaBase() {


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
    public void seRecuperaUnEspirituDadoUnId() {

        Espiritu unEspiritu = espirituDao.crear(espirituM);
        assertNotNull(unEspiritu.getId());
        System.out.println(unEspiritu);

        Espiritu espirituRecuperado = espirituDao.recuperar(unEspiritu.getId());
        System.out.println(espirituRecuperado);

        assertNotNull(espirituRecuperado.getId());
    }


    @AfterEach
    void tearDown() {
        // TODO implementar eliminar
        //espirituDao.eliminar(espirituM.getId());
    }
}
