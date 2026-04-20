package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlRecuperarMediumException;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateMediumServiceTest {
    private final HibernateMediumDAO mediumDao = new HibernateMediumDAO();
    private final MediumServiceImpl serviceMedium = new MediumServiceImpl(mediumDao);
    private Medium epifanio;
    private Medium ubeda;

    @BeforeEach
    public void setUp() {

        epifanio = new Medium("Epifanio el poderoso", 150, 50);
        ubeda = new Medium("Ubeda el dt", 100, 20);
    }

    @Test
    void deberiaAsignarIdAlCrearUnEspiritu() {
        Medium medium = HibernateTransactionRunner.runTrx(() ->
                serviceMedium.crear(epifanio)
        );

        assertNotNull(medium.getId());
    }


    @Test
    void deberiaRecuperarCorrectamenteLosDatosDelEspiritu() {
        Medium mediumRecuperado = HibernateTransactionRunner.runTrx(() -> {
            Medium mediumEpi = serviceMedium.crear(epifanio);
            return serviceMedium.recuperar(mediumEpi.getId());
        });

        assertEquals(epifanio.getId(), mediumRecuperado.getId());
        assertEquals(epifanio.getNombre(), mediumRecuperado.getNombre());
        assertEquals(epifanio.getMana(), mediumRecuperado.getMana());
        assertEquals(epifanio.getManaMax(), mediumRecuperado.getManaMax());
        assertEquals(epifanio.getCantFortalece(), mediumRecuperado.getCantFortalece());
        assertEquals(0, epifanio.getEspiritus().size());
        assertEquals(0, mediumRecuperado.getEspiritus().size());
    }

    @Test
    void noSeEncuentraElMediumNoPersistido() {
        Long id = 999L;
        assertThrows(ErrorAlRecuperarMediumException.class, () -> {
            serviceMedium.recuperar(id);
        });
    }

    @Test
    void deberiaEliminarYNoPoderRecuperarloLuego() {

        Long id = HibernateTransactionRunner.runTrx(() -> serviceMedium.crear(ubeda).getId());

        this.executeBlockInPersistentContext(() -> serviceMedium.eliminar(id));

        assertThrows(ErrorAlRecuperarMediumException.class, () -> {
            serviceMedium.recuperar(id);
        });
    }

    private void executeBlockInPersistentContext(Runnable block) {
        HibernateTransactionRunner.runTrx(() -> {
            block.run();
            return null;
        });
    }
   
    @AfterEach
    void tearDown() {
        HibernateTransactionRunner.runTrx(() -> {
            mediumDao.eliminarTodos();
            return null;
        });
    }
}
