package ar.edu.unq.epersgeist.persistencia;

import ar.edu.unq.epersgeist.HibernateTest;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateUbicacionDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateUbicacionDAOTest extends HibernateTest {
    private UbicacionDAO ubicacionDAO;

    @BeforeEach
    public void setUp() {
        ubicacionDAO = new HibernateUbicacionDAO();
    }

    // ============================== GUARDAR ==============================

    @Test
    public void debeGuardarUbicacion() {
        this.runTrx(() -> {
            Ubicacion ubicacion = new Ubicacion("Santuario de la Luz");
            Ubicacion ubicacionGuardada = ubicacionDAO.guardar(ubicacion);

            assertNotNull(ubicacionGuardada.getId());
            assertEquals("Santuario de la Luz", ubicacionGuardada.getNombre());
        });
    }

    // ============================== RECUPERAR POR NOMBRE ==============================

    @Test
    public void debeRecuperarUbicacionPorNombre() {
        this.runTrx(() -> {
            Ubicacion ubicacion = new Ubicacion("Santuario de la Luz");
            ubicacionDAO.guardar(ubicacion);

            Ubicacion ubicacionEncontrada = ubicacionDAO.recuperarPorNombre("Santuario de la Luz");

            assertNotNull(ubicacionEncontrada);
            assertEquals("Santuario de la Luz", ubicacionEncontrada.getNombre());
        });
    }

    @Test
    public void debeRetornarNullSiNoExisteUbicacionConEseNombre() {
        this.runTrx(() -> {
            Ubicacion ubicacionEncontrada = ubicacionDAO.recuperarPorNombre("Inexistente");

            assertNull(ubicacionEncontrada);
        });
    }

    @Test
    void recuperarPorNombreNoConfundeUbicaciones() {
        runTrx(() -> {
            ubicacionDAO.guardar(new Ubicacion("Santuario de la Luz"));
            ubicacionDAO.guardar(new Ubicacion("Catedral de la Oscuridad"));

            Ubicacion ubicacionEncontrada = ubicacionDAO.recuperarPorNombre("Santuario de la Luz");

            assertNotNull(ubicacionEncontrada);
            assertEquals("Santuario de la Luz", ubicacionEncontrada.getNombre());
        });
    }

    // ============================== ELIMINAR TODOS ==============================

    @Test
    void debeEliminarTodasLasUbicaciones() {
        runTrx(() -> {
            ubicacionDAO.guardar(new Ubicacion("Santuario de la Luz"));
            ubicacionDAO.guardar(new Ubicacion("Catedral de la Oscuridad"));

            ubicacionDAO.eliminarTodos();

            Ubicacion ubicacion1 = ubicacionDAO.recuperarPorNombre("Santuario de la Luz");
            Ubicacion ubicacion2 = ubicacionDAO.recuperarPorNombre("Catedral de la Oscuridad");

            assertNull(ubicacion1);
            assertNull(ubicacion2);
        });
    }

    @Test
    void eliminarTodosNoFallaSiNoHayUbicaciones() {
        runTrx(() -> {
            ubicacionDAO.eliminarTodos();

            Ubicacion ubicacionEncontrada = ubicacionDAO.recuperarPorNombre("Santuario de la Luz");
            assertNull(ubicacionEncontrada);
        });
    }

    // ============================== TEARDOWN ==============================

    @AfterEach
    void tearDown() {
        this.runTrx(() -> ubicacionDAO.eliminarTodos());
    }
}
