package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.NombreUbicacionInvalidoException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionYaExisteException;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateUbicacionDAO;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateUbicacionServiceTest {
    private UbicacionService ubicacionService;

    @BeforeEach
    void setUp() {
        this.ubicacionService = new UbicacionServiceImpl(new HibernateUbicacionDAO());
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
        runTrx(() -> {
            Ubicacion ubicacion1 = ubicacionService.crear("Santuario de la Luz");
            Ubicacion ubicacion2 = ubicacionService.crear("Cripta de los Susurros");

            assertNotNull(ubicacion1.getId());
            assertNotNull(ubicacion2.getId());

            assertEquals("Santuario de la Luz", ubicacion1.getNombre());
            assertEquals("Cripta de los Susurros", ubicacion2.getNombre());
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
        runTrx(() -> {
            assertThrows(NombreUbicacionInvalidoException.class, () ->
                    ubicacionService.crear(null)
            );
        });
    }

    @Test
    void debeLanzarExcepcionSiElNombreEsVacio() {
        runTrx(() -> {
            assertThrows(NombreUbicacionInvalidoException.class, () ->
                    ubicacionService.crear("")
            );
        });
    }

    @Test
    void debeLanzarExcepcionSiElNombreEsSoloEspacios() {
        runTrx(() -> {
            assertThrows(NombreUbicacionInvalidoException.class, () ->
                    ubicacionService.crear("   ")
            );
        });
    }

    @Test
    void debeLanzarExcepcionSiElNombreYaExisteAunqueTengaEspaciosAlrededor() {
        runTrx(() -> {
            ubicacionService.crear("Santuario de la Luz");

            assertThrows(UbicacionYaExisteException.class, () ->
                    ubicacionService.crear("  Santuario de la Luz  ")
            );
        });
    }

    // ============================== HELPERS ==============================

    private void runTrx(Runnable block) {
        HibernateTransactionRunner.runTrx(() -> {
            block.run();
            return null;
        });
    }

    // ============================== TEARDOWN ==============================

    @AfterEach
    void tearDown() {
        // TODO
    }
}
