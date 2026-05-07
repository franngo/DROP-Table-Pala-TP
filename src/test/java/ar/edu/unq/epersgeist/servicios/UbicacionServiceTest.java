package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionYaExisteException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UbicacionServiceTest {
    private final String NOMBRE_TEMPLO = "Templo del Alba Eterna";
    private final String NOMBRE_CRIPTA = "Cripta de los Susurros";
    private final Ubicacion templo = new Ubicacion(NOMBRE_TEMPLO);
    private final Ubicacion cripta = new Ubicacion(NOMBRE_CRIPTA);
    private final Long ubicacionIdInexistente = 999L;
    Ubicacion berazategui = new Ubicacion("Berazategui");
    private final Espiritu guerrero = new Angelical("Espíritu de la Luz", berazategui);
    private final Espiritu mago = new Angelical("Espíritu de la Oscuridad", berazategui);
    private final Medium epifanio = new Medium("epifanio", 150, 50, berazategui);
    private final Medium ubeda = new Medium("ubeda", 100, 20, berazategui);
    @Autowired
    private UbicacionService ubicacionService;
    @Autowired
    private EspirituService espirituService;
    @Autowired
    private MediumService mediumService;

    @BeforeEach
    void setUp() {


    }

    // ============================== CREAR ==============================

    @Test
    void debeCrearUbicacion() {
        Ubicacion ubicacionCreada = ubicacionService.crear(templo);

        assertNotNull(ubicacionCreada.getId());
        assertEquals(NOMBRE_TEMPLO, ubicacionCreada.getNombre());
    }

    @Test
    void debeCrearVariasUbicaciones() {
        Ubicacion ubicacionCreada1 = ubicacionService.crear(templo);
        Ubicacion ubicacionCreada2 = ubicacionService.crear(cripta);

        assertNotNull(ubicacionCreada1.getId());
        assertNotNull(ubicacionCreada2.getId());

        assertEquals(NOMBRE_TEMPLO, ubicacionCreada1.getNombre());
        assertEquals(NOMBRE_CRIPTA, ubicacionCreada2.getNombre());
    }

    @Test
    void noDebeCrearUbicacionConNombreDuplicado() {
        ubicacionService.crear(templo);

        assertThrows(
                UbicacionYaExisteException.class,
                () -> ubicacionService.crear(templo)
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
    void noDebeRecuperarUbicacionConIdInexistente() {
        assertThrows(
                UbicacionNoEncontradaException.class,
                () -> ubicacionService.recuperar(ubicacionIdInexistente)
        );
    }

    // ============================== RECUPERAR TODOS ==============================

    @Test
    void debeRecuperarTodasLasUbicaciones() {
        ubicacionService.crear(templo);
        ubicacionService.crear(cripta);

        List<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();

        assertEquals(2, ubicaciones.size());
        assertTrue(ubicaciones.stream().anyMatch(u -> Objects.equals(u.getNombre(), NOMBRE_TEMPLO)));
        assertTrue(ubicaciones.stream().anyMatch(u -> Objects.equals(u.getNombre(), NOMBRE_CRIPTA)));
    }

    @Test
    void debeRecuperarListaVaciaSiNoHayUbicaciones() {
        List<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();

        assertEquals(0, ubicaciones.size());
    }

    // ============================== ACTUALIZAR ==============================

    @Test
    void debeActualizarUbicacion() {
        Ubicacion ubicacionCreada = ubicacionService.crear(templo);

        Ubicacion ubicacionModificada = new Ubicacion(NOMBRE_CRIPTA);
        ubicacionModificada.setId(ubicacionCreada.getId());

        ubicacionService.actualizar(ubicacionModificada);

        Ubicacion ubicacionActualizada = ubicacionService.recuperar(ubicacionCreada.getId());

        assertEquals(ubicacionModificada.getId(), ubicacionActualizada.getId());
        assertEquals(ubicacionModificada.getNombre(), ubicacionActualizada.getNombre());
    }

    @Test
    void noDebeActualizarUbicacionConIdNull() {
        ubicacionService.crear(templo);

        // No se asigna el ID a la ubicación modificada, lo que simula un intento de actualización con ID null
        Ubicacion ubicacionModificada = new Ubicacion(NOMBRE_CRIPTA);

        assertThrows(
                UbicacionNoEncontradaException.class,
                () -> ubicacionService.actualizar(ubicacionModificada)
        );
    }

    @Test
    void noDebeActualizarUbicacionInexistente() {
        assertThrows(
                UbicacionNoEncontradaException.class,
                () -> ubicacionService.actualizar(templo)

        );
    }
    
    // ============================== ELIMINAR ==============================

    @Test
    void debeEliminarUbicacion() {
        Ubicacion ubicacionCreada = ubicacionService.crear(templo);
        Long ubicacionId = ubicacionCreada.getId();

        ubicacionService.eliminar(ubicacionId);

        assertThrows(
                UbicacionNoEncontradaException.class,
                () -> ubicacionService.recuperar(ubicacionId)
        );
    }

    @Test
    void noDebeEliminarUbicacionConIdInexistente() {
        ubicacionService.eliminar(ubicacionIdInexistente);

        assertThrows(
                UbicacionNoEncontradaException.class,
                () -> ubicacionService.recuperar(ubicacionIdInexistente)
        );
    }

    // ============================== ELIMINAR TODOS ==============================

    @Test
    void debeEliminarTodasLasUbicaciones() {
        ubicacionService.crear(templo);
        ubicacionService.crear(cripta);

        ubicacionService.eliminarTodos();

        assertTrue(ubicacionService.recuperarTodos().isEmpty());
    }

    @Test
    void debeEliminarTodasLasUbicacionesCuandoNoHayUbicaciones() {
        ubicacionService.eliminarTodos();

        assertTrue(ubicacionService.recuperarTodos().isEmpty());
    }

    // ============================== ESPIRITUS EN ==============================

    @Transactional
    @Test
    void debeRecuperarEspiritusEnUbicacion() {
        Ubicacion ubicacionCreada = ubicacionService.crear(cripta);

        // Crear espíritus y persistirlos
        espirituService.crear(guerrero);
        espirituService.crear(mago);

        // Asignar espíritus a la ubicación
        ubicacionCreada.agregarEspiritu(guerrero);
        ubicacionCreada.agregarEspiritu(mago);

        // Actualizar la ubicación para guardar los cambios
        ubicacionService.actualizar(ubicacionCreada);

        List<Espiritu> espiritusEnUbicacion = ubicacionService.espiritusEn(ubicacionCreada.getId());

        assertEquals(2, espiritusEnUbicacion.size());
    }

    @Test
    void espiritusEnLanzaExcepcionSiUbicacionNoExiste() {

        assertThrows(
                UbicacionNoEncontradaException.class,
                () -> ubicacionService.espiritusEn(999123L)

        );
    }

    // ============================== MEDIUMS SIN ESPIRITUS EN ==============================
    @Transactional

    @Test
    void debeObtenerMediumsSinEspiritus() {
        ubicacionService.crear(cripta);
        // ubicacionService.crear(templo);


        mediumService.crear(epifanio);
        mediumService.crear(ubeda);

        cripta.agregarMedium(epifanio);
        cripta.agregarMedium(ubeda);
        ubicacionService.actualizar(cripta);

        List<Medium> resultado = ubicacionService.mediumsSinEspiritusEn(cripta.getId());

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(u -> Objects.equals(u.getId(), epifanio.getId())));
        assertTrue(resultado.stream().anyMatch(u -> Objects.equals(u.getId(), ubeda.getId())));
    }

    @Transactional
    @Test
    void deberiaObtener1MediumSinEspiritus() {

        ubicacionService.crear(cripta);

        mediumService.crear(epifanio);
        mediumService.crear(ubeda);

        // Ubicarlos en la cripta
        cripta.agregarMedium(epifanio);
        cripta.agregarMedium(ubeda);
        ubicacionService.actualizar(cripta);

        // Crear espíritu en la cripta
        Espiritu chiquiTapa = new Angelical("Chiqui", cripta);
        espirituService.crear(chiquiTapa);

        // Conectar el espíritu a epifanio
        espirituService.conectar(chiquiTapa.getId(), epifanio.getId());

        // Ejecutar
        List<Medium> resultado = ubicacionService.mediumsSinEspiritusEn(cripta.getId());

        // Verificar
        assertEquals(1, resultado.size());

        assertTrue(resultado.stream()
                .noneMatch(m -> Objects.equals(m.getId(), epifanio.getId())));

        assertTrue(resultado.stream()
                .anyMatch(m -> Objects.equals(m.getId(), ubeda.getId())));
    }


    @Test
    void deberiaRetornarListaVaciaSiNoHayMediumsSinEspiritus() {
        Ubicacion ubicacion = ubicacionService.crear(templo);

        List<Medium> resultado = ubicacionService.mediumsSinEspiritusEn(ubicacion.getId());

        assertEquals(0, resultado.size());
    }


    // ============================== TEARDOWN ==============================

    @AfterEach
    void tearDown() {
        espirituService.eliminarTodos();
        mediumService.eliminarTodos();
        ubicacionService.eliminarTodos();
    }
}
