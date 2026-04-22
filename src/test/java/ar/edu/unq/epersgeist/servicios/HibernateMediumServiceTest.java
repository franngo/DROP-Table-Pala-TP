package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlActualizarMediumException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlRecuperarEspirituException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlRecuperarMediumException;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateUbicacionDAO;
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
    private Espiritu espirituM;
    private Espiritu espirituM2;

    @BeforeEach
    public void setUp() {
        epifanio = new Medium("Epifanio el poderoso", 150, 50);
        ubeda = new Medium("Ubeda el dt", 100, 20);
        espirituM = new Espiritu("Fuego", "Charizard");
        espirituM2 = new Espiritu("Agua", "Bulbasor");
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

    @Test
    public void descansarCasoExitoso() {
        assertEquals(50, epifanio.getMana());
        assertEquals(0, espirituM.getNivelDeConexion());
        assertEquals(0, espirituM2.getNivelDeConexion());

        epifanio.agregarEspiritu(espirituM);
        epifanio.agregarEspiritu(espirituM2);
        this.executeBlockInPersistentContext(() -> {
            mediumDao.crear(epifanio);
            espirituDao.crear(espirituM);
            espirituDao.crear(espirituM2);
        });

        serviceMedium.descansar(epifanio.getId());

        this.executeBlockInPersistentContext(() -> {
            Medium m = mediumDao.recuperar(epifanio.getId());
            Espiritu e1 = espirituDao.recuperar(espirituM.getId());
            Espiritu e2 = espirituDao.recuperar(espirituM2.getId());

            assertEquals(65, m.getMana());
            assertEquals(5, e1.getNivelDeConexion());
            assertEquals(5, e2.getNivelDeConexion());
        });
    }


    @Test
    public void descansarFallaPorMediumIdNulo() {
        ErrorAlRecuperarMediumException exception = assertThrows(ErrorAlRecuperarMediumException.class,
                () -> serviceMedium.descansar(2L));
        assertEquals("No se pudo recuperar al medium con id " + "2" + ".", exception.getMessage());
    }

    @Test
    public void invocarCasoExitoso() {
        HibernateUbicacionDAO ubicacionDao = new HibernateUbicacionDAO();
        Ubicacion ubicacion = new Ubicacion("Quilmes");
        epifanio.setUbicacion(ubicacion);

        assertEquals(50, epifanio.getMana());
        assertNull(espirituM.getUbicacion());
        assertEquals(0, ubicacion.getEspiritus().size());

        this.executeBlockInPersistentContext(() -> {
            mediumDao.crear(epifanio);
            espirituDao.crear(espirituM);
            ubicacionDao.guardar(ubicacion);
        });

        serviceMedium.invocar(epifanio.getId(), espirituM.getId());

        this.executeBlockInPersistentContext(() -> {
            Ubicacion u = ubicacionDao.recuperar(ubicacion.getId());
            Medium m = mediumDao.recuperar(epifanio.getId());
            Espiritu e = espirituDao.recuperar(espirituM.getId());

            assertEquals(40, m.getMana());
            assertEquals(m.getUbicacion(), e.getUbicacion());
            assertEquals(1, u.getEspiritus().size());
            assertTrue(u.getEspiritus().contains(e));
        });

        this.executeBlockInPersistentContext(() -> {
            espirituDao.eliminarTodos();
            mediumDao.eliminarTodos();
            ubicacionDao.eliminarTodos();
        });
    }

    @Test
    public void invocarFallaPorMediumIdNulo() {
        ErrorAlRecuperarMediumException exception = assertThrows(ErrorAlRecuperarMediumException.class,
                () -> serviceMedium.invocar(1L, 2L));
        assertEquals("No se pudo recuperar al medium con id " + "1" + ".", exception.getMessage());
    }


    @Test
    public void invocarFallaPorEspirituIdNulo() {
        this.executeBlockInPersistentContext(() -> {
            mediumDao.crear(epifanio);
        });

        Long idInexistente = epifanio.getId() + 1;
        ErrorAlRecuperarEspirituException exception = assertThrows(ErrorAlRecuperarEspirituException.class,
                () -> serviceMedium.invocar(epifanio.getId(), idInexistente));
        assertEquals("No se pudo recuperar al espíritu con id " + idInexistente + ".", exception.getMessage());
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
            espirituDao.eliminarTodos();
            mediumDao.eliminarTodos();
            return null;
        });
    }
}
