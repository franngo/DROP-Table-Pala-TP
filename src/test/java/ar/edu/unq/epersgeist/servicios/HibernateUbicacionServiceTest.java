package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.HibernateTest;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlRecuperarMediumException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.*;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateUbicacionDAO;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateUbicacionServiceTest extends HibernateTest {
    private EspirituDAO espirituDAO;
    private UbicacionDAO ubicacionDAO;
    private UbicacionService ubicacionService;
    private final HibernateMediumDAO mediumDao = new HibernateMediumDAO();
    private final MediumServiceImpl serviceMedium = new MediumServiceImpl(mediumDao, espirituDAO);

    @BeforeEach
    void setUp() {
        this.espirituDAO = new HibernateEspirituDAO();
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

    @Test
    void debeRecuperarUnaListaVaciaSiNoHayUbicaciones() {
        this.runTrx(() -> {
            List<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();

            assertEquals(0, ubicaciones.size());
        });
    }

    // ============================== ESPIRITUS EN ==============================

    @Test
    void espiritusEnRetornaListaDeEspiritus() {
        this.runTrx(() -> {
            Ubicacion ubicacion = ubicacionService.crear("Santuario de la Luz");

            Espiritu guerrero = espirituDAO.guardar(new Espiritu("Guerrero", "Espíritu de la Luz"));
            Espiritu mago = espirituDAO.guardar(new Espiritu("Mago", "Espíritu de la Oscuridad"));

            ubicacion.agregarEspiritu(guerrero);
            ubicacion.agregarEspiritu(mago);

            List<Espiritu> espiritusEnUbicacion = ubicacionService.espiritusEn(ubicacion.getId());

            assertEquals(2, espiritusEnUbicacion.size());
            assertTrue(espiritusEnUbicacion.contains(guerrero));
            assertTrue(espiritusEnUbicacion.contains(mago));
        });
    }

    @Test
    void espiritusEnLanzaExcepcionSiUbicacionNoExiste() {
        this.runTrx(() ->
            assertThrows(UbicacionNoEncontradaException.class, () ->
                    ubicacionService.espiritusEn(999L)
            )
        );
    }

    @Test
    void espiritusEnLanzaExcepcionSiIdEsNull() {
        this.runTrx(() ->
            assertThrows(IdUbicacionInvalidoException.class, () ->
                    ubicacionService.espiritusEn(null)
            )
        );
    }

    // ============================== Actualizar ==============================

    @Test
    void deberiaActualizarSoloElNombre(){
        Long id = HibernateTransactionRunner.runTrx(() -> ubicacionService.crear("Santuario de la Luz").getId());
        Ubicacion ubicacionMod = new Ubicacion("Catedral de la Oscuridad");
        ubicacionMod.setId(id);

        this.executeBlockInPersistentContext(() -> ubicacionService.actualizar(ubicacionMod));

        Ubicacion ubicacionActualizada = HibernateTransactionRunner.runTrx(() -> ubicacionService.recuperar(id));

        assertEquals(ubicacionMod.getId(), ubicacionActualizada.getId());
    }

    @Test
    void noDeberiaActualizarUbicacionSinId(){
        this.runTrx(() ->
                assertThrows(ErrorAlActualizarUbicacionException.class, () ->
                        ubicacionService.actualizar(null)
                )
        );
    }


    private void executeBlockInPersistentContext(Runnable block) {
        HibernateTransactionRunner.runTrx(() -> {
            block.run();
            return null;
        });
    }

    // ============================== Eliminar ==============================

    @Test
    void deberiaEliminarLaUbicacionYNoPoderRecuperarlaLuego() {

        Long id = HibernateTransactionRunner.runTrx(() -> ubicacionService.crear("Santuario de la Luz")).getId();



        this.executeBlockInPersistentContext(() -> ubicacionService.eliminar(id));
        Ubicacion ubicacion = HibernateTransactionRunner.runTrx(() -> ubicacionService.recuperar(id));

        assertNull(ubicacion);
    }

    // ============================== mediumsSinEspiritusEn ==============================

    @Test
    void deberiaObtenerMediumsSinEspiritus() {

        this.runTrx(() -> {
            Ubicacion ubicacion = ubicacionService.crear("Santuario de la Luz");

            Medium epifanio = new Medium("Epifanio el poderoso", 150, 50);
            Medium ubeda = new Medium("Ubeda el dt", 100, 20);

            epifanio.setUbicacion(ubicacion);
            ubeda.setUbicacion(ubicacion);

            ubicacion.agregarMedium(epifanio);
            ubicacion.agregarMedium(ubeda);
            List<Medium> resultado = ubicacionService.mediumsSinEspiritusEn(ubicacion.getId());

            assertEquals(2, resultado.size());
        });
    }

    @Test
    void deberiaObtener1MediumsSinEspiritus() {

        this.runTrx(() -> {
            Ubicacion ubicacion = ubicacionService.crear("Santuario de la Luz");
            Medium epifanio = new Medium("Epifanio el poderoso", 150, 50);
            Medium ubeda = new Medium("Ubeda el dt", 100, 20);
            Espiritu mago = espirituDAO.guardar(new Espiritu("Mago", "Espíritu de la Oscuridad"));
            ubeda.agregarEspiritu(mago);

            ubicacion.agregarMedium(epifanio);
            ubicacion.agregarMedium(ubeda);

            List<Medium> resultado = ubicacionService.mediumsSinEspiritusEn(ubicacion.getId());

            assertEquals(1, resultado.size());
        });
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHayMediumsSinEspiritus() {

        this.runTrx(() -> {
            Ubicacion ubicacion = ubicacionService.crear("Santuario de la Luz");

            List<Medium> resultado = ubicacionService.mediumsSinEspiritusEn(ubicacion.getId());

            assertEquals(0, resultado.size());
        });
    }

    @Test
    void mediumsSinEspiritusEnLanzaExceptionSiElIdNoExiste(){
        this.runTrx(() ->
                assertThrows(IdUbicacionInvalidoException.class, () ->
                        ubicacionService.mediumsSinEspiritusEn(
                                null)
                )
        );
    }



    // ============================== TEARDOWN ==============================




    @AfterEach
    void tearDown() {
        this.runTrx(() -> {
            espirituDAO.eliminarTodos();
            mediumDao.eliminarTodos();
            ubicacionDAO.eliminarTodos();
        });
    }
}
