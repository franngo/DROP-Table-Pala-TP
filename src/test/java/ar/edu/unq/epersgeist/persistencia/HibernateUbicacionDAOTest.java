package ar.edu.unq.epersgeist.persistencia;

import ar.edu.unq.epersgeist.HibernateTest;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateUbicacionDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateUbicacionDAOTest extends HibernateTest {
    private UbicacionDAO ubicacionDAO;
    private EspirituDAO espirituDAO;

    @BeforeEach
    public void setUp() {
        ubicacionDAO = new HibernateUbicacionDAO();
        espirituDAO = new HibernateEspirituDAO();
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

    // ============================== RECUPERAR ==============================

    @Test
    void debeRecuperarUbicacionPorId() {
        this.runTrx(() -> {
            Ubicacion ubicacionGuardada = ubicacionDAO.guardar(new Ubicacion("Santuario de la Luz"));

            Ubicacion ubicacionRecuperada = ubicacionDAO.recuperar(ubicacionGuardada.getId());

            assertNotNull(ubicacionRecuperada);
            assertEquals(ubicacionGuardada.getId(), ubicacionRecuperada.getId());
            assertEquals("Santuario de la Luz", ubicacionRecuperada.getNombre());
        });
    }

    @Test
    void debeRetornarNullSiNoExisteUbicacionConEseId() {
        this.runTrx(() -> {
            Ubicacion ubicacionRecuperada = ubicacionDAO.recuperar(999L);

            assertNull(ubicacionRecuperada);
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
            Ubicacion ubicacionEncontrada = ubicacionDAO.recuperarPorNombre("Santuario de la Luz");

            assertNull(ubicacionEncontrada);
        });
    }

    @Test
    void recuperarPorNombreNoConfundeUbicaciones() {
        this.runTrx(() -> {
            ubicacionDAO.guardar(new Ubicacion("Santuario de la Luz"));
            ubicacionDAO.guardar(new Ubicacion("Catedral de la Oscuridad"));

            Ubicacion ubicacionEncontrada = ubicacionDAO.recuperarPorNombre("Santuario de la Luz");

            assertNotNull(ubicacionEncontrada);
            assertEquals("Santuario de la Luz", ubicacionEncontrada.getNombre());
        });
    }

    // ============================== RECUPERAR TODOS ==============================

    @Test
    void debeRecuperarTodasLasUbicaciones() {
        this.runTrx(() -> {
            ubicacionDAO.guardar(new Ubicacion("Santuario de la Luz"));
            ubicacionDAO.guardar(new Ubicacion("Catedral de la Oscuridad"));

            List<Ubicacion> ubicaciones = ubicacionDAO.recuperarTodos();
            List<String> nombres = ubicaciones.stream().map(Ubicacion::getNombre).toList();

            assertEquals(2, ubicaciones.size());
            assertTrue(nombres.contains("Santuario de la Luz"));
            assertTrue(nombres.contains("Catedral de la Oscuridad"));
        });
    }

    @Test
    void debeRetornarListaVaciaSiNoHayUbicaciones() {
        this.runTrx(() -> {
            List<Ubicacion> ubicaciones = ubicacionDAO.recuperarTodos();

            assertNotNull(ubicaciones);
            assertTrue(ubicaciones.isEmpty());
        });
    }

    // ============================== ELIMINAR TODOS ==============================

    @Test
    void debeEliminarTodasLasUbicaciones() {
        this.runTrx(() -> {
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
        this.runTrx(() -> {
            ubicacionDAO.eliminarTodos();

            Ubicacion ubicacionEncontrada = ubicacionDAO.recuperarPorNombre("Santuario de la Luz");
            assertNull(ubicacionEncontrada);
        });
    }

    // ============================== EXTRAS ==============================

    @Test
    void debePersistirCambioDeRelacionSinUpdateExplcito() {
        final Long[] ubicacionId = new Long[1];

        // Primera transacción: se crea la relación y Hibernate la persiste al commit
        this.runTrx(() -> {
            Ubicacion ubicacion = ubicacionDAO.guardar(new Ubicacion("Santuario de la Luz"));
            Espiritu guerrero = espirituDAO.guardar(new Espiritu("Guerrero", "Espíritu de la Luz"));

            ubicacion.agregarEspiritu(guerrero);

            // No se invoca guardar ni actualizar explícitamente
            // Hibernate detecta el cambio en la relación (dirty checking)
            // y lo sincroniza automáticamente al hacer commit

            ubicacionId[0] = ubicacion.getId();
        });

        // Segunda transacción: se recupera desde la DB y se verifica que se guardó
        this.runTrx(() -> {
            Ubicacion ubicacion = ubicacionDAO.recuperar(ubicacionId[0]);

            assertEquals(1, ubicacion.getEspiritus().size());
        });
    }

    @Test
    void debePersistirVariosEspiritusEnUnaUbicacion() {
        final Ubicacion[] ubicacion = new Ubicacion[1];

        this.runTrx(() ->
            ubicacion[0] = ubicacionDAO.guardar(new Ubicacion("Santuario de la Luz"))
        );

        this.runTrx(() -> {
            Espiritu guerrero = espirituDAO.guardar(new Espiritu("Guerrero", "Espíritu de la Luz"));
            ubicacion[0].agregarEspiritu(guerrero);
        });

        this.runTrx(() -> {
            Espiritu mago = espirituDAO.guardar(new Espiritu("Mago", "Espíritu de la Oscuridad"));
            ubicacion[0].agregarEspiritu(mago);
        });

        this.runTrx(() -> {
            Ubicacion ubicacionGuardada = ubicacionDAO.recuperar(ubicacion[0].getId());

            assertEquals(2, ubicacionGuardada.getEspiritus().size());
        });
    }

    // ============================== TEARDOWN ==============================

    @AfterEach
    void tearDown() {
        this.runTrx(() -> {
            espirituDAO.eliminarTodos();
            ubicacionDAO.eliminarTodos();
        });
    }
}
