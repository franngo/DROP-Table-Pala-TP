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
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateUbicacionServiceTest {
    private static final String NOMBRE_TEMPLO = "Templo del Alba Eterna";
    private static final String NOMBRE_CRIPTA = "Cripta de los Susurros";

    private final Ubicacion templo = new Ubicacion(NOMBRE_TEMPLO);
    private final Ubicacion cripta = new Ubicacion(NOMBRE_CRIPTA);

    private final Espiritu guerrero = new Espiritu("Espíritu de la Luz");
    private final Espiritu mago = new Espiritu("Espíritu de la Oscuridad");

    private final Medium epifanio = new Medium("epifanio", 150, 50);
    private final Medium ubeda = new Medium("ubeda", 100, 20);

    private UbicacionService ubicacionService;
    private EspirituService espirituService;
    private MediumService mediumService;

    @BeforeEach
    void setUp() {
        MediumDAO mediumDao = new HibernateMediumDAO();
        EspirituDAO espirituDAO = new HibernateEspirituDAO();
        UbicacionDAO ubicacionDAO = new HibernateUbicacionDAO();

        ubicacionService = new UbicacionServiceImpl(ubicacionDAO);
        espirituService = new EspirituServiceImpl(espirituDAO, mediumDao);
        mediumService = new MediumServiceImpl(mediumDao, espirituDAO);
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
        assertTrue(ubicaciones.stream().anyMatch(u -> Objects.equals(u.getId(), templo.getId())));
        assertTrue(ubicaciones.stream().anyMatch(u -> Objects.equals(u.getId(), cripta.getId())));
    }

    @Test
    void debeRecuperarUnaListaVaciaSiNoHayUbicaciones() {
        List<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();

        assertEquals(0, ubicaciones.size());
    }

    // ============================== ESPIRITUS EN ==============================

    @Test
    void espiritusEnRetornaListaDeEspiritus() {
        Ubicacion ubicacion = ubicacionService.crear(cripta);

        espirituService.crear(guerrero);
        espirituService.crear(mago);

        ubicacion.agregarEspiritu(guerrero);
        ubicacion.agregarEspiritu(mago);
        ubicacionService.actualizar(ubicacion);

        List<Espiritu> espiritusEnUbicacion;
        espiritusEnUbicacion = ubicacionService.espiritusEn(ubicacion.getId());

        assertEquals(2, espiritusEnUbicacion.size());
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

        Long id = ubicacionService.crear(templo).getId();

        Ubicacion ubicacionMod = new Ubicacion("Catedral de la Oscuridad");
        ubicacionMod.setId(id);

        ubicacionService.actualizar(ubicacionMod);

        Ubicacion ubicacionActualizada = ubicacionService.recuperar(id);

        assertEquals(ubicacionMod.getId(), ubicacionActualizada.getId());
        assertEquals(ubicacionMod.getNombre(), ubicacionActualizada.getNombre());
    }

    @Test
    void noDeberiaActualizarUbicacionSinId() {
        assertThrows(
                ErrorAlActualizarUbicacionException.class,
                () -> ubicacionService.actualizar(null)

        );
    }

    @Test
    void noDeberiaActualizarUbicacionNoPersistida() {
        assertThrows(
                ErrorAlActualizarUbicacionException.class,
                () -> ubicacionService.actualizar(templo)

        );
    }

    // ============================== ELIMINAR ==============================

    @Test
    void debeEliminarLaUbicacionYNoPoderRecuperarlaLuego() {
        Ubicacion ubicacionCreada = ubicacionService.crear(templo);
        Long ubicacionId = ubicacionCreada.getId();


        ubicacionService.eliminar(ubicacionId);


        Ubicacion ubicacion = ubicacionService.recuperar(ubicacionId);

        assertNull(ubicacion);
    }

    // ============================== mediumsSinEspiritusEn ==============================

    @Test
    void deberiaObtenerTodosMediumsSinEspiritus() {

        Ubicacion ubicacion = ubicacionService.crear(cripta);
        espirituService.crear(guerrero);
        mediumService.crear(epifanio);
        mediumService.crear(ubeda);

        ubicacion.agregarMedium(epifanio);
        ubicacion.agregarMedium(ubeda);
        ubicacionService.actualizar(ubicacion);

        List<Medium> resultado = ubicacionService.mediumsSinEspiritusEn(ubicacion.getId());

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(u -> Objects.equals(u.getId(), epifanio.getId())));
        assertTrue(resultado.stream().anyMatch(u -> Objects.equals(u.getId(), ubeda.getId())));
    }

    @Test
    void deberiaObtener1MediumSinEspiritus() {
        Ubicacion ubicacion = ubicacionService.crear(cripta);
        espirituService.crear(guerrero);
        mediumService.crear(epifanio);
        mediumService.crear(ubeda);

        ubicacion.agregarMedium(epifanio);
        ubicacion.agregarMedium(ubeda);
        epifanio.agregarEspiritu(guerrero);
        mediumService.actualizar(epifanio);
        ubicacionService.actualizar(ubicacion);


        List<Medium> resultado = ubicacionService.mediumsSinEspiritusEn(ubicacion.getId());

        assertEquals(1, resultado.size());
        assertTrue(resultado.stream().noneMatch(u -> Objects.equals(u.getId(), epifanio.getId())));
        assertTrue(resultado.stream().anyMatch(u -> Objects.equals(u.getId(), ubeda.getId())));
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
        espirituService.eliminarTodos();
        mediumService.eliminarTodos();
        ubicacionService.eliminarTodos();
    }
}
