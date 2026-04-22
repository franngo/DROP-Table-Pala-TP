package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlActualizarMediumException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlRecuperarMediumException;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateMediumServiceTest {
    private final HibernateMediumDAO mediumDao = new HibernateMediumDAO();
    private final HibernateEspirituDAO espirituDao = new HibernateEspirituDAO();
    private final MediumServiceImpl serviceMedium = new MediumServiceImpl(mediumDao, espirituDao);
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
    void deberiaActualizarSoloLosCamposModificados1() {

        Long id = HibernateTransactionRunner.runTrx(() -> serviceMedium.crear(epifanio).getId());

        Medium modificado = new Medium("Rolando el audaz", 120, 50);
        modificado.setId(id);
        modificado.setCantFortalece(15);

        //como modificado tiene el mismo id que epifanio, actualiza dicha fila en la BD
        this.executeBlockInPersistentContext(() -> serviceMedium.actualizar(modificado));

        Medium actualizado = HibernateTransactionRunner.runTrx(() -> serviceMedium.recuperar(id));

        assertEquals(modificado.getId(), actualizado.getId());
        assertEquals(modificado.getNombre(), actualizado.getNombre());
        assertEquals(modificado.getMana(), actualizado.getMana());
        assertEquals(modificado.getManaMax(), actualizado.getManaMax());
        assertEquals(modificado.getCantFortalece(), actualizado.getCantFortalece());
    }

    @Test
    void deberiaActualizarSoloLosCamposModificados2() {

        Long id = HibernateTransactionRunner.runTrx(() -> serviceMedium.crear(epifanio).getId());

        epifanio.setNombre("Rolando el audaz");
        epifanio.setMana(120);
        epifanio.setCantFortalece(15);

        //se actualiza la fila con el id de epifanio en la DB
        this.executeBlockInPersistentContext(() -> serviceMedium.actualizar(epifanio));

        Medium actualizado = HibernateTransactionRunner.runTrx(() -> serviceMedium.recuperar(id));

        assertEquals(epifanio.getId(), actualizado.getId());
        assertEquals(epifanio.getNombre(), actualizado.getNombre());
        assertEquals(epifanio.getMana(), actualizado.getMana());
        assertEquals(epifanio.getManaMax(), actualizado.getManaMax());
        assertEquals(epifanio.getCantFortalece(), actualizado.getCantFortalece());
    }

    @Test
    void noDeberiaActualizarConIdNulo() {
        ErrorAlActualizarMediumException exception =
        assertThrows(ErrorAlActualizarMediumException.class, () -> {
            HibernateTransactionRunner.runTrx(() -> {
                serviceMedium.actualizar(epifanio); //comienza sin id
                return null;
            });
        });
        assertEquals("No se puede actualizar un medium que no tiene id.", exception.getMessage());
    }

    @Test
    void noDeberiaActualizarConIdQueNoSeEncuentraEnBD() {
        ErrorAlActualizarMediumException exception =
        assertThrows(ErrorAlActualizarMediumException.class, () -> {
            HibernateTransactionRunner.runTrx(() -> {
                epifanio.setId(1L); //no hay nada en la DB con id 1, porque arranca vacía
                serviceMedium.actualizar(epifanio);
                return null;
            });
        });
        assertEquals("No existe en la BD el medium con id: 1", exception.getMessage());
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
