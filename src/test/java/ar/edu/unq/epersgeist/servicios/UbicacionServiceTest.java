package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Cementerio;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionYaExisteException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UbicacionServiceTest {
    // --------------------------------------------------------------------------------
    // CONSTANTES
    // --------------------------------------------------------------------------------

    private static final String NOMBRE_SANTUARIO = "Santuario de Lumen Silente";
    private static final String NOMBRE_CEMENTERIO = "Cementerio de Nox Umbra";

    private static final Integer ENERGIA_VALIDA = 50;
    private static final Integer ENERGIA_MAXIMA = 100;

    private static final Long ID_INEXISTENTE = 999_999_999L;

    // --------------------------------------------------------------------------------
    // DEPENDENCIAS
    // --------------------------------------------------------------------------------

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private EspirituService espirituService;

    @Autowired
    private MediumService mediumService;

    // --------------------------------------------------------------------------------
    // ATRIBUTOS
    // --------------------------------------------------------------------------------

    private Ubicacion santuario;
    private Ubicacion cementerio;

    // --------------------------------------------------------------------------------
    // SETUP Y TEARDOWN
    // --------------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        santuario = new Santuario(NOMBRE_SANTUARIO, ENERGIA_VALIDA);
        cementerio = new Cementerio(NOMBRE_CEMENTERIO, ENERGIA_VALIDA);
    }

    @AfterEach
    void tearDown() {
        ubicacionService.eliminarTodos();
    }

    // --------------------------------------------------------------------------------
    // CREAR
    // --------------------------------------------------------------------------------

    @Nested
    class Crear {
        @Test
        void sePuedeCrearUnaUbicacion() {
            Ubicacion ubicacionCreada = ubicacionService.crear(santuario);

            assertNotNull(ubicacionCreada.getId());
            assertEquals(NOMBRE_SANTUARIO, ubicacionCreada.getNombre());
        }

        @Test
        void noSePuedeCrearUbicacionConNombreRepetido() {
            ubicacionService.crear(santuario);

            assertThrows(
                    UbicacionYaExisteException.class,
                    () -> ubicacionService.crear(santuario)
            );
        }
    }

    // --------------------------------------------------------------------------------
    // RECUPERAR
    // --------------------------------------------------------------------------------

    @Nested
    class Recuperar {
        @Test
        void sePuedeRecuperarUnaUbicacionPorId() {
            Ubicacion ubicacionCreada = ubicacionService.crear(santuario);

            Ubicacion ubicacionRecuperada = ubicacionService.recuperar(ubicacionCreada.getId());

            assertNotNull(ubicacionRecuperada);
            assertEquals(NOMBRE_SANTUARIO, ubicacionRecuperada.getNombre());
            assertEquals(ENERGIA_VALIDA, ubicacionRecuperada.getEnergia());
        }

        @Test
        void recuperarUbicacionInexistenteLanzaExcepcion() {
            assertThrows(
                    UbicacionNoEncontradaException.class,
                    () -> ubicacionService.recuperar(ID_INEXISTENTE)
            );
        }
    }

    // --------------------------------------------------------------------------------
    // RECUPERAR TODOS
    // --------------------------------------------------------------------------------

    @Nested
    class RecuperarTodos {
        @Test
        void sePuedenRecuperarTodasLasUbicaciones() {
            ubicacionService.crear(santuario);
            ubicacionService.crear(cementerio);

            List<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();

            assertEquals(2, ubicaciones.size());
            assertTrue(ubicaciones.stream().anyMatch(u -> Objects.equals(u.getNombre(), NOMBRE_SANTUARIO)));
            assertTrue(ubicaciones.stream().anyMatch(u -> Objects.equals(u.getNombre(), NOMBRE_CEMENTERIO)));
        }

        @Test
        void recuperarTodosCuandoNoHayUbicacionesDevuelveListaVacia() {
            List<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();

            assertTrue(ubicaciones.isEmpty());
        }
    }

    // --------------------------------------------------------------------------------
    // ACTUALIZAR
    // --------------------------------------------------------------------------------

    @Nested
    class Actualizar {
        @Test
        void sePuedeActualizarUnaUbicacion() {
            Ubicacion ubicacionCreada = ubicacionService.crear(santuario);

            ubicacionCreada.setEnergia(ENERGIA_MAXIMA);
            ubicacionService.actualizar(ubicacionCreada);

            Ubicacion ubicacionRecuperada = ubicacionService.recuperar(ubicacionCreada.getId());

            assertEquals(ENERGIA_MAXIMA, ubicacionRecuperada.getEnergia());
        }

        @Test
        void actualizarUbicacionInexistenteLanzaExcepcion() {
            santuario.setId(ID_INEXISTENTE);

            assertThrows(
                    UbicacionNoEncontradaException.class,
                    () -> ubicacionService.actualizar(santuario)
            );
        }

        @Test
        void actualizarUbicacionConIdNullLanzaExcepcion() {
            assertThrows(
                    UbicacionNoEncontradaException.class,
                    () -> ubicacionService.actualizar(santuario)
            );
        }

        @Test
        void actualizarMantieneNombreYModificaEnergia() {
            Ubicacion ubicacionCreada = ubicacionService.crear(santuario);

            ubicacionCreada.setEnergia(ENERGIA_MAXIMA);
            ubicacionService.actualizar(ubicacionCreada);

            Ubicacion ubicacionRecuperada = ubicacionService.recuperar(ubicacionCreada.getId());

            assertEquals(NOMBRE_SANTUARIO, ubicacionRecuperada.getNombre());
            assertEquals(ENERGIA_MAXIMA, ubicacionRecuperada.getEnergia());
        }
    }

    // --------------------------------------------------------------------------------
    // ELIMINAR
    // --------------------------------------------------------------------------------

    @Nested
    class Eliminar {
        @Test
        void sePuedeEliminarUnaUbicacionPorId() {
            Ubicacion ubicacionCreada = ubicacionService.crear(santuario);

            ubicacionService.eliminar(ubicacionCreada.getId());

            List<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();

            assertTrue(ubicaciones.isEmpty());
        }

        @Test
        void eliminarUbicacionInexistenteNoLanzaExcepcion() {
            assertDoesNotThrow(() -> ubicacionService.eliminar(ID_INEXISTENTE));
        }
    }

    // --------------------------------------------------------------------------------
    // ELIMINAR TODOS
    // --------------------------------------------------------------------------------

    @Nested
    class EliminarTodos {
        @Test
        void sePuedenEliminarTodasLasUbicaciones() {
            ubicacionService.crear(santuario);
            ubicacionService.crear(cementerio);

            ubicacionService.eliminarTodos();

            List<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();

            assertTrue(ubicaciones.isEmpty());
        }

        @Test
        void eliminarTodosCuandoNoHayUbicacionesNoFalla() {
            assertDoesNotThrow(() -> ubicacionService.eliminarTodos());
        }
    }

    // --------------------------------------------------------------------------------
    // ESPIRITUS EN UBICACION
    // --------------------------------------------------------------------------------

    @Nested
    class EspiritusEn {
        private static final String NOMBRE_ANGEL = "Espíritu Ángel";
        private static final String NOMBRE_DEMONIO = "Espíritu Demonio";

        private Ubicacion santuarioPersistido;

        // --------------------------------------------------------------------------------

        @BeforeEach
        void setUp() {
            santuarioPersistido = ubicacionService.crear(santuario);

            Ubicacion cementerioPersistido = ubicacionService.crear(cementerio);

            espirituService.crear(new Angelical(NOMBRE_ANGEL, santuarioPersistido));
            espirituService.crear(new Demonio(NOMBRE_DEMONIO, cementerioPersistido));
        }

        @AfterEach
        void tearDown() {
            espirituService.eliminarTodos();
            ubicacionService.eliminarTodos();
        }

        // --------------------------------------------------------------------------------

        @Test
        void devuelveEspiritusDeUnaUbicacion() {
            List<Espiritu> espiritus = ubicacionService.espiritusEn(santuarioPersistido.getId());

            assertEquals(1, espiritus.size());
            assertEquals(NOMBRE_ANGEL, espiritus.getFirst().getNombre());
        }

        @Test
        void devuelveListaVaciaSiNoHayEspiritusEnLaUbicacion() {
            Ubicacion nuevaUbicacion = ubicacionService.crear(new Santuario("Otro santuario", ENERGIA_VALIDA));

            List<Espiritu> espiritus = ubicacionService.espiritusEn(nuevaUbicacion.getId());

            assertTrue(espiritus.isEmpty());
        }

        @Test
        void devuelveListaVaciaSiLaUbicacionNoExiste() {
            List<Espiritu> espiritus = ubicacionService.espiritusEn(ID_INEXISTENTE);

            assertTrue(espiritus.isEmpty());
        }
    }

    // --------------------------------------------------------------------------------
    // MEDIUMS SIN ESPIRITUS EN UBICACION
    // --------------------------------------------------------------------------------

    @Nested
    class MediumsSinEspiritusEn {
        private static final String NOMBRE_MEDIUM_CON_ESPIRITUS = "Medium con Espíritus";
        private static final String NOMBRE_MEDIUM_SIN_ESPIRITUS = "Medium sin Espíritus";

        private static final String NOMBRE_ANGEL = "Espíritu Ángel";

        private Ubicacion santuarioPersistido;

        // --------------------------------------------------------------------------------

        @BeforeEach
        void setUp() {
            santuarioPersistido = ubicacionService.crear(santuario);

            mediumService.crear(
                    new Medium(NOMBRE_MEDIUM_SIN_ESPIRITUS, 100, 50, santuarioPersistido)
            );

            Medium mediumConEspiritus = mediumService.crear(
                    new Medium(NOMBRE_MEDIUM_CON_ESPIRITUS, 100, 50, santuarioPersistido)
            );
            Espiritu espiritu = espirituService.crear(
                    new Angelical(NOMBRE_ANGEL, santuarioPersistido)
            );
            espirituService.conectar(espiritu.getId(), mediumConEspiritus.getId());
        }


        @AfterEach
        void tearDown() {
            espirituService.eliminarTodos();
            mediumService.eliminarTodos();
            ubicacionService.eliminarTodos();
        }
        // --------------------------------------------------------------------------------

        @Test
        void devuelveMediumsSinEspiritusEnLaUbicacion() {
            List<Medium> mediums = ubicacionService.mediumsSinEspiritusEn(santuarioPersistido.getId());

            assertEquals(1, mediums.size());
            assertEquals(NOMBRE_MEDIUM_SIN_ESPIRITUS, mediums.getFirst().getNombre());
        }

        @Test
        void devuelveListaVaciaSiLaUbicacionNoTieneMediums() {
            Ubicacion otraUbicacion = ubicacionService.crear(new Santuario("Santuario Vacío", ENERGIA_VALIDA));

            List<Medium> mediums = ubicacionService.mediumsSinEspiritusEn(otraUbicacion.getId());

            assertTrue(mediums.isEmpty());
        }


        @Test
        void devuelveListaVaciaSiLaUbicacionNoExiste() {
            List<Medium> mediums = ubicacionService.mediumsSinEspiritusEn(ID_INEXISTENTE);

            assertTrue(mediums.isEmpty());
        }
    }
}

// --------------------------------------------------------------------------------
// PRUEBAS NEO4J
// --------------------------------------------------------------------------------

@SpringBootTest
class Neo4jTesting {
    // --------------------------------------------------------------------------------
    // CONSTANTES
    // --------------------------------------------------------------------------------

    private static final String NOMBRE_SANTUARIO = "Santuario de Lumen Silente";
    private static final String NOMBRE_CEMENTERIO = "Cementerio de Nox Umbra";

    private static final Integer ENERGIA_VALIDA = 50;
    private static final Integer ENERGIA_MAXIMA = 100;

    private static final Long ID_INEXISTENTE = 999_999_999L;

    // --------------------------------------------------------------------------------
    // DEPENDENCIAS
    // --------------------------------------------------------------------------------

    @Autowired
    private UbicacionService ubicacionService;

    // --------------------------------------------------------------------------------
    // ATRIBUTOS
    // --------------------------------------------------------------------------------

    private Ubicacion santuario;
    private Ubicacion cementerio;

    // --------------------------------------------------------------------------------
    // SETUP Y TEARDOWN
    // --------------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        santuario = new Santuario(NOMBRE_SANTUARIO, ENERGIA_VALIDA);
        cementerio = new Cementerio(NOMBRE_CEMENTERIO, ENERGIA_VALIDA);
    }

    @AfterEach
    void tearDown() {
        //ubicacionService.eliminarTodos();
    }

    @Test
    void sePuedeCrearUnaUbicacion() {
        Ubicacion ubicacionCreada = ubicacionService.crear(cementerio);
    }
}
