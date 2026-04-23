package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.*;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateUbicacionDAO;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateUbicacionServiceTest {
    private MediumDAO mediumDao;
    private EspirituDAO espirituDAO;
    private UbicacionDAO ubicacionDAO;

    private static final String NOMBRE_TEMPLO = "Templo del Alba Eterna";
    private static final String NOMBRE_CRIPTA = "Cripta de los Susurros";

    private final Ubicacion templo = new Ubicacion(NOMBRE_TEMPLO);
    private final Ubicacion cripta = new Ubicacion(NOMBRE_CRIPTA);

    private EspirituService espirituService;
    private UbicacionService ubicacionService;

    @BeforeEach
    void setUp() {
        mediumDao = new HibernateMediumDAO();
        espirituDAO = new HibernateEspirituDAO();
        ubicacionDAO = new HibernateUbicacionDAO();

        ubicacionService = new UbicacionServiceImpl(ubicacionDAO);
        espirituService = new EspirituServiceImpl(espirituDAO);
    }

    // ============================== CREAR ==============================

    @Test
    void debeCrearUbicacion() {
        Ubicacion ubicacionCreada = ubicacionService.crear(templo);

        assertNotNull(ubicacionCreada.getId());
        assertEquals(NOMBRE_TEMPLO, ubicacionCreada.getNombre());
    }

    @Test
    void debeCrearMultiplesUbicacionesConNombresDistintos() {
        Ubicacion ubicacionCreada1 = ubicacionService.crear(templo);
        Ubicacion ubicacionCreada2 = ubicacionService.crear(cripta);

        assertNotNull(ubicacionCreada1.getId());
        assertNotNull(ubicacionCreada2.getId());

        assertEquals(NOMBRE_TEMPLO, ubicacionCreada1.getNombre());
        assertEquals(NOMBRE_CRIPTA, ubicacionCreada2.getNombre());
    }

    @Test
    void debeLanzarExcepcionSiElNombreYaExiste() {
        ubicacionService.crear(templo);

        assertThrows(
                UbicacionYaExisteException.class,
                () -> ubicacionService.crear(templo)
        );
    }

    @Test
    void debeLanzarExcepcionSiLaUbicacionEsNull() {
        assertThrows(
                UbicacionInvalidaException.class,
                () -> ubicacionService.crear(null)
        );
    }

    @Test
    void debeLanzarExcepcionSiElNombreEsVacio() {
        assertThrows(
                NombreUbicacionInvalidoException.class,
                () -> ubicacionService.crear(new Ubicacion(""))
        );
    }

    @Test
    void debeLanzarExcepcionSiElNombreEsSoloEspacios() {
        assertThrows(
                NombreUbicacionInvalidoException.class,
                () -> ubicacionService.crear(new Ubicacion("   "))
        );
    }

    @Test
    void debeLanzarExcepcionSiElNombreYaExisteAunqueTengaEspaciosAlrededor() {
        ubicacionService.crear(templo);

        assertThrows(
                UbicacionYaExisteException.class,
                () -> ubicacionService.crear(new Ubicacion("   " + NOMBRE_TEMPLO + "   "))
        );
    }

    // ============================== RECUPERAR ==============================

    @Test
    void debeRecuperarUbicacionPorId() {
        Ubicacion ubicacionCreada = ubicacionService.crear(templo);

        Ubicacion ubicacionRecuperada = ubicacionService.recuperar(ubicacionCreada.getId());

        assertNotNull(ubicacionRecuperada);
        assertEquals(NOMBRE_TEMPLO, ubicacionRecuperada.getNombre());
    }

    @Test
    void debeLanzarExcepcionSiElIdEsNull() {
        assertThrows(
                IdUbicacionInvalidoException.class,
                () -> ubicacionService.recuperar(null)
        );
    }

    @Test
    void debeRetornarNullSiNoExisteLaUbicacion() {
        Ubicacion ubicacionRecuperada = ubicacionService.recuperar(999L);

        assertNull(ubicacionRecuperada);
    }

    // ============================== RECUPERAR TODOS ==============================

    @Test
    void debeRecuperarTodasLasUbicaciones() {
        ubicacionService.crear(templo);
        ubicacionService.crear(cripta);

        List<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();

        assertEquals(2, ubicaciones.size());
    }

    @Test
    void debeRecuperarUnaListaVaciaSiNoHayUbicaciones() {
        List<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();

        assertEquals(0, ubicaciones.size());
    }

    // ============================== ESPIRITUS EN ==============================

    @Test
    void espiritusEnRetornaListaDeEspiritus() {
        Ubicacion ubicacionCreada = ubicacionService.crear(templo);

        Espiritu orion = espirituService.crear(new Espiritu("Orion el Errante"));
        Espiritu neros = espirituService.crear(new Espiritu("Neros el Oscuro"));

        ubicacionCreada.agregarEspiritu(orion);
        ubicacionCreada.agregarEspiritu(neros);

        List<Espiritu> espiritusEnUbicacion = ubicacionService.espiritusEn(ubicacionCreada.getId());

        assertEquals(2, espiritusEnUbicacion.size());
        assertTrue(espiritusEnUbicacion.contains(orion));
        assertTrue(espiritusEnUbicacion.contains(neros));
    }

    @Test
    void espiritusEnLanzaExcepcionSiUbicacionNoExiste() {
        assertThrows(
                UbicacionNoEncontradaException.class,
                () -> ubicacionService.espiritusEn(999L)
        );
    }

    @Test
    void espiritusEnLanzaExcepcionSiIdEsNull() {
        assertThrows(
                IdUbicacionInvalidoException.class,
                () -> ubicacionService.espiritusEn(null)
        );
    }

    // ============================== ACTUALIZAR ==============================

    @Test
    void deberiaActualizarSoloElNombre() {
        Long id = HibernateTransactionRunner.runTrx(() -> ubicacionService.crear(templo).getId());
        Ubicacion ubicacionMod = new Ubicacion("Catedral de la Oscuridad");
        ubicacionMod.setId(id);

        ubicacionService.actualizar(ubicacionMod);

        Ubicacion ubicacionActualizada = ubicacionService.recuperar(id);

        assertEquals(ubicacionMod.getId(), ubicacionActualizada.getId());
    }

    @Test
    void noDeberiaActualizarUbicacionSinId(){
        assertThrows(
                ErrorAlActualizarUbicacionException.class,
                () -> ubicacionService.actualizar(null)
        );
    }

    // ============================== ELIMINAR ==============================

    @Test
    void debeliminarLaUbicacionYNoPoderRecuperarlaLuego() {
        Ubicacion ubicacionCreada = ubicacionService.crear(templo);
        Long ubicacionId = ubicacionCreada.getId();

        ubicacionService.eliminar(ubicacionId);

        Ubicacion ubicacion = HibernateTransactionRunner.runTrx(
                () -> ubicacionService.recuperar(ubicacionId)
        );

        assertNull(ubicacion);
    }

    // ============================== mediumsSinEspiritusEn ==============================

    @Test
    void deberiaObtenerMediumsSinEspiritus() {
        Ubicacion ubicacion = ubicacionService.crear(templo);

        Medium epifanio = new Medium("Epifanio el poderoso", 150, 50);
        Medium ubeda = new Medium("Ubeda el dt", 100, 20);

        epifanio.setUbicacion(ubicacion);
        ubeda.setUbicacion(ubicacion);

        ubicacion.agregarMedium(epifanio);
        ubicacion.agregarMedium(ubeda);
        List<Medium> resultado = ubicacionService.mediumsSinEspiritusEn(ubicacion.getId());

        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaObtener1MediumsSinEspiritus() {
         Ubicacion ubicacion = ubicacionService.crear(templo);
         Medium epifanio = new Medium("Epifanio el poderoso", 150, 50);
         Medium ubeda = new Medium("Ubeda el dt", 100, 20);
         Espiritu mago = espirituDAO.guardar(new Espiritu("Espíritu de la Oscuridad"));
         ubeda.agregarEspiritu(mago);

         ubicacion.agregarMedium(epifanio);
         ubicacion.agregarMedium(ubeda);

         List<Medium> resultado = ubicacionService.mediumsSinEspiritusEn(ubicacion.getId());

         assertEquals(1, resultado.size());
    }


    @Test
    void deberiaRetornarListaVaciaSiNoHayMediumsSinEspiritus() {
         Ubicacion ubicacion = ubicacionService.crear(templo);

         List<Medium> resultado = ubicacionService.mediumsSinEspiritusEn(ubicacion.getId());

         assertEquals(0, resultado.size());
    }

    @Test
    void mediumsSinEspiritusEnLanzaExceptionSiElIdNoExiste() {
        assertThrows(
                IdUbicacionInvalidoException.class,
                () -> ubicacionService.mediumsSinEspiritusEn(null)
        );
    }

    // ============================== TEARDOWN ==============================

    @AfterEach
    void tearDown() {
        HibernateTransactionRunner.runTrx(() -> {
            mediumDao.eliminarTodos();
            espirituDAO.eliminarTodos();
            ubicacionDAO.eliminarTodos();

            return null;
        });
    }
}
