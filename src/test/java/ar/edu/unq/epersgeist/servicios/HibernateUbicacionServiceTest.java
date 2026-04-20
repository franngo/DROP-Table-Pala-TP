package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.HibernateTest;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.IdUbicacionInvalidoException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.NombreUbicacionInvalidoException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionYaExisteException;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateUbicacionDAO;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateUbicacionServiceTest extends HibernateTest {
    private UbicacionDAO ubicacionDAO;
    private UbicacionService ubicacionService;

    @BeforeEach
    void setUp() {
        this.ubicacionDAO = new HibernateUbicacionDAO();
        this.ubicacionService = new UbicacionServiceImpl(this.ubicacionDAO);
    }

    // ============================== CREAR ==============================

    @Test
    void debeCrearUbicacion() {
        this.runTrx(() -> {
            Ubicacion ubicacion = ubicacionService.crear("Santuario de la Luz");

            assertNotNull(ubicacion.getId());
            assertEquals("Santuario de la Luz", ubicacion.getNombre());
        });
    }

    @Test
    void debeCrearMultiplesUbicacionesConNombresDistintos() {
        this.runTrx(() -> {
            Ubicacion ubicacion1 = ubicacionService.crear("Santuario de la Luz");
            Ubicacion ubicacion2 = ubicacionService.crear("Catedral de la Oscuridad");

            assertNotNull(ubicacion1.getId());
            assertNotNull(ubicacion2.getId());

            assertEquals("Santuario de la Luz", ubicacion1.getNombre());
            assertEquals("Catedral de la Oscuridad", ubicacion2.getNombre());
        });
    }

    @Test
    void debeLanzarExcepcionSiElNombreYaExiste() {
        this.runTrx(() -> {
            ubicacionService.crear("Santuario de la Luz");

            assertThrows(UbicacionYaExisteException.class, () ->
                    ubicacionService.crear("Santuario de la Luz")
            );
        });
    }

    @Test
    void debeLanzarExcepcionSiElNombreEsNull() {
        this.runTrx(() ->
            assertThrows(NombreUbicacionInvalidoException.class, () ->
                    ubicacionService.crear(null)
            ));
    }

    @Test
    void debeLanzarExcepcionSiElNombreEsVacio() {
        this.runTrx(() ->
            assertThrows(NombreUbicacionInvalidoException.class, () ->
                    ubicacionService.crear("")
            ));
    }

    @Test
    void debeLanzarExcepcionSiElNombreEsSoloEspacios() {
        this.runTrx(() ->
            assertThrows(NombreUbicacionInvalidoException.class, () ->
                    ubicacionService.crear("   ")
            ));
    }

    @Test
    void debeLanzarExcepcionSiElNombreYaExisteAunqueTengaEspaciosAlrededor() {
        this.runTrx(() -> {
            ubicacionService.crear("Santuario de la Luz");

            assertThrows(UbicacionYaExisteException.class, () ->
                    ubicacionService.crear("  Santuario de la Luz  ")
            );
        });
    }

    // ============================== RECUPERAR ==============================

    @Test
    void debeRecuperarUbicacionPorId() {
        this.runTrx(() -> {
            Ubicacion ubicacionCreada = ubicacionService.crear("Santuario de la Luz");

            Ubicacion ubicacionRecuperada = ubicacionService.recuperar(ubicacionCreada.getId());

            assertNotNull(ubicacionRecuperada);
            assertEquals("Santuario de la Luz", ubicacionRecuperada.getNombre());
        });
    }

    @Test
    void debeLanzarExcepcionSiElIdEsNull() {
        this.runTrx(() ->
            assertThrows(IdUbicacionInvalidoException.class, () ->
                    ubicacionService.recuperar(null)
            ));
    }

    @Test
    void debeRetornarNullSiNoExisteLaUbicacion() {
        this.runTrx(() -> {
            Ubicacion ubicacionRecuperada = ubicacionService.recuperar(999L);

            assertNull(ubicacionRecuperada);
        });
    }

    // ============================== RECUPERAR TODOS ==============================

    @Test
    void debeRecuperarTodasLasUbicaciones() {
        this.runTrx(() -> {
            ubicacionService.crear("Santuario de la Luz");
            ubicacionService.crear("Catedral de la Oscuridad");

            List<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();

            assertEquals(2, ubicaciones.size());
        });
    }

    // ============================== TEARDOWN ==============================

    @AfterEach
    void tearDown() {
        this.runTrx(() -> ubicacionDAO.eliminarTodos());
    }
}
