package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.HarmonicResult;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Cementerio;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.*;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.persistencia.raven.RegistroHistoricoDAO;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Map;
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

    @Autowired
    private RegistroHistoricoDAO registroHistoricoService;

    // --------------------------------------------------------------------------------
    // ATRIBUTOS
    // --------------------------------------------------------------------------------

    private Ubicacion santuario;
    private Ubicacion cementerio;

    private List<Coordenada> coordenadas;
    private Coordenada coordenadaEspiritu;
    private List<Coordenada> coordenadas2;
    private Coordenada coordenadaEspiritu2;
    private List<Coordenada> coordenadas3;
    private List<Coordenada> coordenadas4;
    private List<Coordenada> coordenadas5;
    private List<Coordenada> coordenadas6;
    private List<Coordenada> coordenadas7;

    // --------------------------------------------------------------------------------
    // SETUP Y TEARDOWN
    // --------------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        santuario = new Santuario(NOMBRE_SANTUARIO, ENERGIA_VALIDA);
        cementerio = new Cementerio(NOMBRE_CEMENTERIO, ENERGIA_VALIDA);

        coordenadas = List.of(
                new Coordenada(-34.81, -58.22),
                new Coordenada(-34.82, -58.24),
                new Coordenada(-34.80, -58.20)
        );

        coordenadaEspiritu = new Coordenada(-34.81, -58.22);

        coordenadas2 = List.of(
                new Coordenada(-34.91, -58.32),
                new Coordenada(-34.92, -58.34),
                new Coordenada(-34.90, -58.30)
        );

        coordenadaEspiritu2 = new Coordenada(-34.91, -58.32);

        coordenadas3 = List.of(
                new Coordenada(-35.01, -58.42),
                new Coordenada(-35.02, -58.44),
                new Coordenada(-35.00, -58.40)
        );

        coordenadas4 = List.of(
                new Coordenada(-35.11, -58.52),
                new Coordenada(-35.12, -58.54),
                new Coordenada(-35.10, -58.50)
        );

        coordenadas5 = List.of(
                new Coordenada(-35.21, -58.62),
                new Coordenada(-35.22, -58.64),
                new Coordenada(-35.20, -58.60)
        );

        coordenadas6 = List.of(
                new Coordenada(-35.31, -58.72),
                new Coordenada(-35.32, -58.74),
                new Coordenada(-35.30, -58.70)
        );

        coordenadas7 = List.of(
                new Coordenada(-35.41, -58.82),
                new Coordenada(-35.42, -58.84),
                new Coordenada(-35.40, -58.80)
        );
    }

    @AfterEach
    void tearDown() {
        ubicacionService.eliminarTodos();
        registroHistoricoService.reiniciarBaseDeDatos();
    }

    // --------------------------------------------------------------------------------
    // CREAR
    // --------------------------------------------------------------------------------

    @Nested
    class Crear {
        @Test
        void sePuedeCrearUnaUbicacion() {
            Ubicacion ubicacionCreada = ubicacionService.crear(santuario, coordenadas);

            assertNotNull(ubicacionCreada.getId());
            assertEquals(NOMBRE_SANTUARIO, ubicacionCreada.getNombre());
        }

        @Test
        void noSePuedeCrearUbicacionConNombreRepetido() {
            ubicacionService.crear(santuario, coordenadas);

            assertThrows(
                    UbicacionYaExisteException.class,
                    () -> ubicacionService.crear(santuario, coordenadas)
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
            Ubicacion ubicacionCreada = ubicacionService.crear(santuario, coordenadas);

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
            ubicacionService.crear(santuario, coordenadas);
            ubicacionService.crear(cementerio, coordenadas2);

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
            Ubicacion ubicacionCreada = ubicacionService.crear(santuario, coordenadas);

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
            Ubicacion ubicacionCreada = ubicacionService.crear(santuario, coordenadas);

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
            Ubicacion ubicacionCreada = ubicacionService.crear(santuario, coordenadas);

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
            ubicacionService.crear(santuario, coordenadas);
            ubicacionService.crear(cementerio, coordenadas2);

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
            santuarioPersistido = ubicacionService.crear(santuario, coordenadas);

            Ubicacion cementerioPersistido = ubicacionService.crear(cementerio, coordenadas2);

            espirituService.crear(new Angelical(NOMBRE_ANGEL, santuarioPersistido), coordenadaEspiritu);
            espirituService.crear(new Demonio(NOMBRE_DEMONIO, cementerioPersistido), coordenadaEspiritu2);
        }

        @AfterEach
        void tearDown() {
            espirituService.eliminarTodos();
            ubicacionService.eliminarTodos();
            registroHistoricoService.reiniciarBaseDeDatos();
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
            Ubicacion nuevaUbicacion = ubicacionService.crear(new Santuario("Otro santuario", ENERGIA_VALIDA), coordenadas3);

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
            santuarioPersistido = ubicacionService.crear(santuario, coordenadas);

            mediumService.crear(
                    new Medium(NOMBRE_MEDIUM_SIN_ESPIRITUS, 100, 50, santuarioPersistido), new Coordenada(-34.81, -58.22)
            );

            Medium mediumConEspiritus = mediumService.crear(
                    new Medium(NOMBRE_MEDIUM_CON_ESPIRITUS, 100, 50, santuarioPersistido), new Coordenada(-34.82, -58.24)
            );
            Espiritu espiritu = espirituService.crear(
                    new Angelical(NOMBRE_ANGEL, santuarioPersistido), new Coordenada(-34.80, -58.20)
            );
            espirituService.conectar(espiritu.getId(), mediumConEspiritus.getId());
        }


        @AfterEach
        void tearDown() {
            espirituService.eliminarTodos();
            mediumService.eliminarTodos();
            ubicacionService.eliminarTodos();
            registroHistoricoService.reiniciarBaseDeDatos();
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
            Ubicacion otraUbicacion = ubicacionService.crear(new Santuario("Santuario Vacío", ENERGIA_VALIDA), coordenadas2);

            List<Medium> mediums = ubicacionService.mediumsSinEspiritusEn(otraUbicacion.getId());

            assertTrue(mediums.isEmpty());
        }

        @Test
        void devuelveListaVaciaSiLaUbicacionNoExiste() {
            List<Medium> mediums = ubicacionService.mediumsSinEspiritusEn(ID_INEXISTENTE);

            assertTrue(mediums.isEmpty());
        }
    }

    // --------------------------------------------------------------------------------
    // CONEXIONES ENTRE UBICACIONES EN NEO4J (conectar() y estanConectadas())
    // --------------------------------------------------------------------------------

    @Nested
    class ConexionesEnNeo4j {
        private Ubicacion origen;
        private Ubicacion destino;
        private Ubicacion otroNodo;

        @BeforeEach
        void setUp() {
            origen = ubicacionService.crear(new Santuario("ORIGEN", 50), coordenadas);
            destino = ubicacionService.crear(new Cementerio("DESTINO", 50), coordenadas2);
            otroNodo = ubicacionService.crear(new Santuario("OTRO", 50), coordenadas3);
        }

        @AfterEach
        void tearDown() {
            ubicacionService.eliminarTodos();
            registroHistoricoService.reiniciarBaseDeDatos();
        }

        // ------------------------------------------------------------
        // CASO 1: conexión básica
        // ------------------------------------------------------------

        @Test
        void conectarDosUbicacionesCreaConexion() {
            ubicacionService.conectar(origen.getId(), destino.getId(), 10L);

            assertTrue(
                    ubicacionService.estanConectadas(origen.getId(), destino.getId())
            );
        }

        // ------------------------------------------------------------
        // CASO 2: dirección de la conexión
        // ------------------------------------------------------------

        @Test
        void conectarEsUnidireccional() {
            ubicacionService.conectar(origen.getId(), destino.getId(), 10L);

            assertTrue(
                    ubicacionService.estanConectadas(origen.getId(), destino.getId())
            );

            assertFalse(
                    ubicacionService.estanConectadas(destino.getId(), origen.getId())
            );
        }

        // ------------------------------------------------------------
        // CASO 3: conectar múltiples veces no rompe el grafo
        // ------------------------------------------------------------

        @Test
        void conectarMismaRelacionNoDuplicaNiRompe() {
            ubicacionService.conectar(origen.getId(), destino.getId(), 10L);
            ubicacionService.conectar(origen.getId(), destino.getId(), 20L);

            assertTrue(
                    ubicacionService.estanConectadas(origen.getId(), destino.getId())
            );
        }

        // ------------------------------------------------------------
        // CASO 4: conexión bidireccional manual
        // ------------------------------------------------------------

        @Test
        void sePuedeCrearConexionBidireccional() {
            ubicacionService.conectar(origen.getId(), destino.getId(), 10L);
            ubicacionService.conectar(destino.getId(), origen.getId(), 15L);

            assertTrue(
                    ubicacionService.estanConectadas(origen.getId(), destino.getId())
            );

            assertTrue(
                    ubicacionService.estanConectadas(destino.getId(), origen.getId())
            );
        }

        // ------------------------------------------------------------
        // CASO 5: conectar con nodo distinto no afecta otras conexiones
        // ------------------------------------------------------------

        @Test
        void conectarNoAfectaOtrosNodos() {
            ubicacionService.conectar(origen.getId(), destino.getId(), 10L);

            assertFalse(
                    ubicacionService.estanConectadas(origen.getId(), otroNodo.getId())
            );

            assertFalse(
                    ubicacionService.estanConectadas(otroNodo.getId(), destino.getId())
            );
        }

        // ------------------------------------------------------------
        // CASO 6: costo no afecta conectividad
        // ------------------------------------------------------------

        @Test
        void costoNoAfectaSiEstanConectados() {
            Long idInexistente = 999_999_999L;

            ubicacionService.conectar(origen.getId(), destino.getId(), idInexistente);

            assertTrue(
                    ubicacionService.estanConectadas(origen.getId(), destino.getId())
            );
        }

        // ------------------------------------------------------------
        // CASO 7: conexión con IDs inexistentes
        // ------------------------------------------------------------

        @Test
        void conectarConIdInexistenteLanzaExcepcion() {
            Long idInexistente = 999_999_999L;

            assertThrows(
                    UbicacionNoEncontradaException.class,
                    () -> ubicacionService.conectar(idInexistente, destino.getId(), 10L)
            );
        }

        @Test
        void conectarConAmbosIdsInexistentesLanzaExcepcion() {
            Long idInexistente1 = 999_999_999L;
            Long idInexistente2 = 777_777_777L;

            assertThrows(
                    UbicacionNoEncontradaException.class,
                    () -> ubicacionService.conectar(idInexistente1, idInexistente2, 10L)
            );
        }

        // ------------------------------------------------------------
        // Las conexiones reflexivas son válidas
        // ------------------------------------------------------------

        @Test
        void conexionesReflexivas() {
            ubicacionService.conectar(origen.getId(), origen.getId(), 10L);

            assertTrue(
                    ubicacionService.estanConectadas(origen.getId(), origen.getId())
            );
        }

        // ------------------------------------------------------------
        // estanConectadas() no funciona por transitividad
        // ------------------------------------------------------------

        @Test
        void estanConectadasNoEsTransitivo() {
            ubicacionService.conectar(origen.getId(), otroNodo.getId(), 10L);
            ubicacionService.conectar(otroNodo.getId(), destino.getId(), 10L);

            assertFalse(
                    ubicacionService.estanConectadas(origen.getId(), destino.getId())
            );
        }

    }

    // --------------------------------------------------------------------------------
    // caminoMasRentable()
    // --------------------------------------------------------------------------------

    @Nested
    class CaminoMasRentable {
        private Ubicacion origen;
        private Ubicacion destino;
        private Ubicacion otroNodo;

        @BeforeEach
        void setUp() {
            origen = ubicacionService.crear(new Santuario("ORIGEN", 50), coordenadas);
            destino = ubicacionService.crear(new Cementerio("DESTINO", 50), coordenadas2);
            otroNodo = ubicacionService.crear(new Santuario("OTRO", 50), coordenadas3);
        }

        @AfterEach
        void tearDown() {
            ubicacionService.eliminarTodos();
            registroHistoricoService.reiniciarBaseDeDatos();
        }

        @Test
        void caminoMasRentableCasoIdOrigenInvalido() {
            Long idNoExistente = 123456789L;

            assertThrows(UbicacionNoEncontradaException.class, () -> ubicacionService.caminoMasRentable(idNoExistente, destino.getId()));
        }

        @Test
        void caminoMasRentableCasoIdDestinoInvalido() {
            Long idNoExistente = 123456789L;

            assertThrows(UbicacionNoEncontradaException.class, () -> ubicacionService.caminoMasRentable(origen.getId(), idNoExistente));
        }

        @Test
        void caminoMasRentableCasoOrigenIgualADestino() {
            List<Ubicacion> ubis = ubicacionService.caminoMasRentable(origen.getId(), origen.getId());

            assertEquals(1, ubis.size());
            assertEquals(origen.getId(), ubis.getFirst().getId());
        }

        @Test
        void caminoMasRentableCasoUbicacionesNoConectadas() {
            //Las Ubicaciones origen y destino NO están conectadas, por lo que solicitar el camino más rentable entre estas dos dará error
            assertThrows(UbicacionesNoConectadasException.class, () -> ubicacionService.caminoMasRentable(origen.getId(), destino.getId()));
        }

        @Test
        void caminoMasRentableCasoExitoso() {
            Ubicacion nodoCuatro = ubicacionService.crear(new Santuario("Nodo cuatro", 50), coordenadas4);

            ubicacionService.conectar(origen.getId(), otroNodo.getId(), 10L);
            ubicacionService.conectar(otroNodo.getId(), destino.getId(), 20L);

            ubicacionService.conectar(origen.getId(), nodoCuatro.getId(), 10L);
            ubicacionService.conectar(nodoCuatro.getId(), destino.getId(), 10L);

            //el camino más rentable de origen a destino es origen-nodoCuatro-destino, que cuesta 20

            List<Ubicacion> ubis = ubicacionService.caminoMasRentable(origen.getId(), destino.getId());

            assertEquals(3, ubis.size());
            assertEquals(origen.getId(), ubis.get(0).getId());
            assertEquals(nodoCuatro.getId(), ubis.get(1).getId());
            assertEquals(destino.getId(), ubis.get(2).getId());
        }

        @Test
        void caminoMasRentableNoSeRigePorLongitudDelCamino() {
            Ubicacion nodoCuatro = ubicacionService.crear(new Santuario("Nodo cuatro", 50), coordenadas4);

            ubicacionService.conectar(origen.getId(), destino.getId(), 100L);

            ubicacionService.conectar(origen.getId(), otroNodo.getId(), 10L);
            ubicacionService.conectar(otroNodo.getId(), nodoCuatro.getId(), 10L);
            ubicacionService.conectar(nodoCuatro.getId(), destino.getId(), 10L);

            //el camino más rentable de origen a destino es origen-otroNodo-nodoCuatro-destino, a pesar de ser más largo en longitud que origen-destino

            List<Ubicacion> ubis = ubicacionService.caminoMasRentable(origen.getId(), destino.getId());

            assertEquals(4, ubis.size());
            assertEquals(origen.getId(), ubis.get(0).getId());
            assertEquals(otroNodo.getId(), ubis.get(1).getId());
            assertEquals(nodoCuatro.getId(), ubis.get(2).getId());
            assertEquals(destino.getId(), ubis.get(3).getId());
        }
    }

    // --------------------------------------------------------------------------------
    // CAMINO MÁS CORTO ENTRE UBICACIONES EN NEO4J
    // --------------------------------------------------------------------------------

    @Nested
    class CaminoMasCortoEnNeo4j {
        private Ubicacion a;
        private Ubicacion b;
        private Ubicacion c;
        private Ubicacion d;

        @BeforeEach
        void setUp() {
            /*
                GRAFO DIRIGIDO:

                A → B → D      (2 saltos) CAMINO MÁS CORTO
                A → C → B → D  (3 saltos)
            */

            a = ubicacionService.crear(new Santuario("A", 50), coordenadas);
            b = ubicacionService.crear(new Santuario("B", 50), coordenadas2);
            c = ubicacionService.crear(new Santuario("C", 50), coordenadas3);
            d = ubicacionService.crear(new Santuario("D", 50), coordenadas4);

            // CAMINO MÁS CORTO
            ubicacionService.conectar(a.getId(), b.getId(), 1L);
            ubicacionService.conectar(b.getId(), d.getId(), 1L);

            // CAMINO MÁS LARGO
            ubicacionService.conectar(a.getId(), c.getId(), 1L);
            ubicacionService.conectar(c.getId(), b.getId(), 1L);
        }

        @AfterEach
        void tearDown() {
            ubicacionService.eliminarTodos();
            registroHistoricoService.reiniciarBaseDeDatos();
        }

        // ------------------------------------------------------------
        // CASO 1: devuelve el camino más corto (2 saltos)
        // ------------------------------------------------------------

        @Test
        void devuelveCaminoMasCorto() {
            List<Ubicacion> camino = ubicacionService.caminoMasCorto(a.getId(), d.getId());

            // C no debe aparecer en el camino porque es parte del camino más largo
            assertFalse(camino.stream().anyMatch(u -> u.getId().equals(c.getId())));

            // CAMINO MÁS CORTO: A → B → D
            assertEquals(3, camino.size());
            assertEquals(a.getId(), camino.get(0).getId());
            assertEquals(b.getId(), camino.get(1).getId());
            assertEquals(d.getId(), camino.get(2).getId());
        }

        // ------------------------------------------------------------
        // CASO 2: origen == destino
        // ------------------------------------------------------------

        @Test
        void origenIgualDestinoDevuelveUnSoloNodo() {
            List<Ubicacion> camino = ubicacionService.caminoMasCorto(a.getId(), a.getId());

            assertEquals(1, camino.size());
            assertEquals(a.getId(), camino.getFirst().getId());
        }

        // ------------------------------------------------------------
        // CASO 3: no hay camino -> excepción
        // ------------------------------------------------------------

        @Test
        void siNoExisteCaminoLanzaExcepcion() {
            Ubicacion sinConexiones = ubicacionService.crear(
                    new Santuario("SIN_CONEXIONES", 50), coordenadas5
            );

            assertThrows(
                    UbicacionesNoConectadasException.class,
                    () -> ubicacionService.caminoMasCorto(a.getId(), sinConexiones.getId())
            );
        }

        // ------------------------------------------------------------
        // CASO 4: respeta dirección del grafo
        // ------------------------------------------------------------

        @Test
        void respetaDireccionDelGrafo() {
            assertThrows(
                    UbicacionesNoConectadasException.class,
                    () -> ubicacionService.caminoMasCorto(d.getId(), a.getId())
            );
        }

        // ------------------------------------------------------------
        // CASO 5: validación de IDs inexistentes
        // ------------------------------------------------------------

        @Test
        void origenInexistenteLanzaExcepcion() {
            Long idInexistente = 999_999_999L;

            assertThrows(
                    UbicacionNoEncontradaException.class,
                    () -> ubicacionService.caminoMasCorto(idInexistente, d.getId())
            );
        }

        @Test
        void destinoInexistenteLanzaExcepcion() {
            Long idInexistente = 999_999_999L;

            assertThrows(
                    UbicacionNoEncontradaException.class,
                    () -> ubicacionService.caminoMasCorto(a.getId(), idInexistente)
            );
        }

        @Test
        void ambosInexistentesLanzaExcepcion() {
            Long idInexistente1 = 999_999_999L;
            Long idInexistente2 = 777_777_777L;

            assertThrows(
                    UbicacionNoEncontradaException.class,
                    () -> ubicacionService.caminoMasCorto(idInexistente1, idInexistente2)
            );
        }
    }

    // --------------------------------------------------------------------------------
    // ubicacionesSobrecargadas()
    // --------------------------------------------------------------------------------


    @Nested
    class UbicacionSobrecargada {

        @BeforeEach
        void setUp() {
            ubicacionService.crear(new Santuario("Alta energia", 70), coordenadas);
            ubicacionService.crear(new Cementerio("Media energia", 50), coordenadas2);
            ubicacionService.crear(new Santuario("Baja energia", 20), coordenadas3);
        }

        @AfterEach
        void tearDown() {
            ubicacionService.eliminarTodos();
            registroHistoricoService.reiniciarBaseDeDatos();
        }

        @Test
        void devuelveUbicacionesConEnergiaSupiorAlUmbral() {
            List<Ubicacion> sobrecargadas = ubicacionService.ubicacionesSobrecargadas(60);

            assertEquals(1, sobrecargadas.size());
            assertEquals("Alta energia", sobrecargadas.getFirst().getNombre());
        }

        @Test
        void devuelveListaVaciaSiNingunaUbicacionSuperaElUmbral() {
            List<Ubicacion> sobrecargadas = ubicacionService.ubicacionesSobrecargadas(80);

            assertTrue(sobrecargadas.isEmpty());
        }

        @Test
        void devuelveTodasSiTodasSuperanElUmbral() {
            ubicacionService.crear(new Santuario("Primera", 90), coordenadas4);
            ubicacionService.crear(new Cementerio("Segunda", 95), coordenadas5);
            List<Ubicacion> sobrecargadas = ubicacionService.ubicacionesSobrecargadas(80);

            assertEquals(2, sobrecargadas.size());
        }

        @Test
        void noDevuelveUbicacionesEliminadas() {
            Ubicacion alta = ubicacionService.crear(new Santuario("Mucha energia", 90), coordenadas4);
            ubicacionService.eliminar(alta.getId());

            List<Ubicacion> sobrecargadas = ubicacionService.ubicacionesSobrecargadas(80);

            assertTrue(sobrecargadas.isEmpty());
        }


        @Test
        void elUmbralEsExclusivoNoIncluyeIguales() {
            ubicacionService.crear(new Santuario("Exacto", 50), coordenadas4);

            List<Ubicacion> sobrecargadas = ubicacionService.ubicacionesSobrecargadas(50);

            assertEquals(1, sobrecargadas.size());
        }
    }

    // --------------------------------------------------------------------------------
    // harmonicResult()
    // --------------------------------------------------------------------------------

    @Nested
    class HarmonicResultTesting {
        private Ubicacion uno;
        private Ubicacion dos;
        private Ubicacion tres;
        private Ubicacion cuatro;
        private Ubicacion cinco;
        private Ubicacion seis;
        private Ubicacion siete;

        @BeforeEach
        void setUp() {
            uno = ubicacionService.crear(new Santuario("uno", 50), coordenadas);
            dos = ubicacionService.crear(new Cementerio("dos", 50), coordenadas2);
            tres = ubicacionService.crear(new Santuario("tres", 50), coordenadas3);
            cuatro = ubicacionService.crear(new Santuario("cuatro", 50), coordenadas4);
            cinco = ubicacionService.crear(new Cementerio("cinco", 50), coordenadas5);
            seis = ubicacionService.crear(new Santuario("seis", 50), coordenadas6);
            siete = ubicacionService.crear(new Santuario("siete", 50), coordenadas7);
        }

        @AfterEach
        void tearDown() {
            ubicacionService.eliminarTodos();
            registroHistoricoService.reiniciarBaseDeDatos();
        }

        @Test
        void harmonicResultFallaUsandoIdInexistente() {
            Long idNoExistente = 123456789L;

            assertThrows(NoTodasLasUbicacionesExistenException.class, () -> ubicacionService.harmonicResult(List.of(idNoExistente)));
        }

        @Test
        void harmonicResultFallaTeniendoTanSoloUnIdInexistente() {
            Long idNoExistente = 123456789L;

            assertThrows(NoTodasLasUbicacionesExistenException.class, () -> ubicacionService.harmonicResult(List.of(uno.getId(), idNoExistente, dos.getId())));
        }

        @Test
        void harmonicResultDevuelveListaVaciaSiLePasamosListaVacia() {
            List<HarmonicResult> ubis = ubicacionService.harmonicResult(List.of());

            assertTrue(ubis.isEmpty());
        }

        @Test
        void harmonicResultCasoExitoso() {

            ubicacionService.conectar(uno.getId(), dos.getId(), 10L);
            ubicacionService.conectar(uno.getId(), tres.getId(), 20L);
            ubicacionService.conectar(dos.getId(), uno.getId(), 20L);
            ubicacionService.conectar(dos.getId(), tres.getId(), 20L);
            ubicacionService.conectar(dos.getId(), cuatro.getId(), 20L);
            ubicacionService.conectar(uno.getId(), cinco.getId(), 10L);
            ubicacionService.conectar(cinco.getId(), seis.getId(), 10L);
            ubicacionService.conectar(seis.getId(), siete.getId(), 10L);

            /*
            Representación del grafo:

                4
               /
              2-----3
             //    /
            1-----/
            \
             5---6---7

             */

            /*
            HCs de los nodos consultados (haciéndolo en orden de los nodos de uno a siete):

            HC de uno: 1 + 1 + 0.5 + 1 + 0.5 + 0.333 = 4.333
            HC de dos: 1 + 1 + 1 + 0.5 + 0.333 + 0.25 = 4.083
            HC de seis: 0 + 0 + 0 + 0 + 0 + 1 = 1
             */

            List<HarmonicResult> ubis = ubicacionService.harmonicResult(List.of(dos.getId(), seis.getId(), uno.getId()));

            HarmonicResult unoHR = ubis.get(0);
            HarmonicResult dosHR = ubis.get(1);
            HarmonicResult seisHR = ubis.get(2);

            assertEquals(uno.getId(), unoHR.id());
            assertEquals(4.333, unoHR.centrality());

            assertEquals(dos.getId(), dosHR.id());
            assertEquals(4.083, dosHR.centrality());

            assertEquals(seis.getId(), seisHR.id());
            assertEquals(1, seisHR.centrality());

        }
    }

    // --------------------------------------------------------------------------------
    // PERSISTENCIA DE COORDENADAS EN MONGO
    // --------------------------------------------------------------------------------


    @Nested
    class PersistenciaMongo {

        @Test
        void sePuedeCrearUbicacionConCoordenadas() {

            Ubicacion ubicacion =
                    ubicacionService.crear(
                            santuario,
                            coordenadas
                    );

            Ubicacion recuperada =
                    ubicacionService.recuperar(
                            ubicacion.getId()
                    );

            assertNotNull(recuperada);
            assertEquals(
                    NOMBRE_SANTUARIO,
                    recuperada.getNombre()
            );
        }

        @Test
        void crearUbicacionConTresCoordenadasFunciona() {

            List<Coordenada> tresCoords = List.of(
                    new Coordenada(-34.81, -58.22),
                    new Coordenada(-34.82, -58.24),
                    new Coordenada(-34.80, -58.20)
            );

            assertDoesNotThrow(() ->
                    ubicacionService.crear(
                            santuario,
                            tresCoords
                    )
            );
        }

        @Test
        void crearUbicacionConMasDeTresCoordenadasFunciona() {

            List<Coordenada> muchasCoords = List.of(
                    new Coordenada(-34.83, -58.21),
                    new Coordenada(-34.82, -58.24),
                    new Coordenada(-34.81, -58.22),
                    new Coordenada(-34.80, -58.20)
            );

            assertDoesNotThrow(() ->
                    ubicacionService.crear(
                            santuario,
                            muchasCoords
                    )
            );
        }

        // ------------------------------------------------------------
        // CASOS DESFAVORABLES
        // ------------------------------------------------------------

        @Test
        void crearUbicacionSinCoordenadasLanzaExcepcion() {

            assertThrows(
                    CoordenadasInsuficientesException.class,
                    () -> ubicacionService.crear(
                            santuario,
                            List.of()
                    )
            );
        }

        @Test
        void crearUbicacionConUnaCoordenadaLanzaExcepcion() {

            List<Coordenada> unaCoord = List.of(
                    new Coordenada(-34.81, -58.22)
            );

            assertThrows(
                    CoordenadasInsuficientesException.class,
                    () -> ubicacionService.crear(
                            santuario,
                            unaCoord
                    )
            );
        }

        @Test
        void crearUbicacionConDosCoordenadasLanzaExcepcion() {

            List<Coordenada> dosCoords = List.of(
                    new Coordenada(-34.81, -58.22),
                    new Coordenada(-34.82, -58.24)
            );

            assertThrows(
                    CoordenadasInsuficientesException.class,
                    () -> ubicacionService.crear(
                            santuario,
                            dosCoords
                    )
            );
        }

        @Test
        void crearUbicacionConCoordenadasNullLanzaExcepcion() {

            assertThrows(
                    CoordenadasInsuficientesException.class,
                    () -> ubicacionService.crear(
                            santuario,
                            null
                    )
            );
        }

        @Test
        void noSePuedeCrearUbicacionConNombreRepetidoAunqueTengaOtrasCoordenadas() {

            ubicacionService.crear(
                    santuario,
                    coordenadas
            );

            List<Coordenada> otrasCoordenadas = List.of(
                    new Coordenada(-10.0, -10.0),
                    new Coordenada(-11.0, -11.0),
                    new Coordenada(-12.0, -12.0)
            );

            assertThrows(
                    UbicacionYaExisteException.class,
                    () -> ubicacionService.crear(
                            new Santuario(
                                    NOMBRE_SANTUARIO,
                                    ENERGIA_VALIDA
                            ),
                            otrasCoordenadas
                    )
            );
        }
    }

    @Nested
    class UbicacionesFrias {

        private static final double UMBRAL = 5.0;
        @Autowired
        private MongoTemplate mongoTemplate;

        @BeforeEach
        void setUp() {
            mongoTemplate.getCollection("datos_sensores").deleteMany(new Document());
        }

        @AfterEach
        void tearDown() {
            mongoTemplate.getCollection("datos_sensores").deleteMany(new Document());
            ubicacionService.eliminarTodos();
            registroHistoricoService.reiniciarBaseDeDatos();
        }

        private void insertTemperatura(String ubicacion, double grados) {
            mongoTemplate.getCollection("datos_sensores")
                    .insertOne(
                            new Document()
                                    .append("ubicacion", ubicacion)
                                    .append("sensor_id", 1)
                                    .append("temperatura",
                                            new Document()
                                                    .append("grados", grados)
                                                    .append("unidad", "°C"))
                    );
        }

        private void insertSonido(String ubicacion, double decibeles) {
            mongoTemplate.getCollection("datos_sensores")
                    .insertOne(
                            new Document()
                                    .append("ubicacion", ubicacion)
                                    .append("sensor_id", 1)
                                    .append("decibeles", decibeles)
                    );
        }

        // ------------------------------------------------------------
        // CASO 1: devuelve una ubicación con promedio menor a 5
        // ------------------------------------------------------------

        @Test
        void devuelveUbicacionesConPromedioMenorACinco() {
            insertTemperatura("A", 2);
            insertTemperatura("A", 4); // promedio = 3

            List<String> resultado = ubicacionService.ubicacionesFrias();

            assertEquals(1, resultado.size());
            assertTrue(resultado.contains("A"));
        }

        // ------------------------------------------------------------
        // CASO 2: no incluye promedio exactamente igual a 5
        // ------------------------------------------------------------

        @Test
        void noIncluyePromedioIgualACinco() {
            insertTemperatura("A", 4);
            insertTemperatura("A", 6); // promedio = 5

            List<String> resultado = ubicacionService.ubicacionesFrias();

            assertTrue(resultado.isEmpty());
        }

        // ------------------------------------------------------------
        // CASO 3: devuelve solo las ubicaciones frías
        // ------------------------------------------------------------

        @Test
        void devuelveSoloUbicacionesFrias() {
            insertTemperatura("A", 0);
            insertTemperatura("A", 2);   // promedio = 1

            insertTemperatura("B", 8);
            insertTemperatura("B", 10);  // promedio = 9

            insertTemperatura("C", -5);
            insertTemperatura("C", 3);   // promedio = -1

            List<String> resultado = ubicacionService.ubicacionesFrias();

            assertEquals(2, resultado.size());
            assertTrue(resultado.contains("A"));
            assertTrue(resultado.contains("C"));
        }

        // ------------------------------------------------------------
        // CASO 4: ignora documentos que no son de temperatura
        // ------------------------------------------------------------

        @Test
        void ignoraDocumentosQueNoSonTemperatura() {
            insertSonido("A", 120);

            List<String> resultado = ubicacionService.ubicacionesFrias();

            assertTrue(resultado.isEmpty());
        }

        // ------------------------------------------------------------
        // CASO 5: mezcla de datos válidos e inválidos
        // ------------------------------------------------------------

        @Test
        void mezclaDeDatosSoloDevuelveUbicacionesFrias() {
            insertTemperatura("A", 0);
            insertTemperatura("A", 4);    // promedio = 2

            insertTemperatura("B", 8);
            insertTemperatura("B", 12);   // promedio = 10

            insertSonido("C", 150);       // debe ignorarse

            List<String> resultado = ubicacionService.ubicacionesFrias();

            assertEquals(1, resultado.size());
            assertTrue(resultado.contains("A"));
        }

        @Test
        void unaMedicionFriaNoAlcanzaSiElPromedioNoEsMenorACinco() {
            insertTemperatura("A", 2);
            insertTemperatura("A", 20); // promedio = 11

            List<String> resultado = ubicacionService.ubicacionesFrias();

            assertTrue(resultado.isEmpty());
        }
    }


    @Nested
    class Sucesos {

        @Autowired
        private MongoTemplate mongoTemplate;

        //Reminder: el tope de decibeles es de 90.0 y el tope de temperatura es de 5.0

        @BeforeEach
        void setUp() {
            mongoTemplate.getCollection("datos_sensores").deleteMany(new Document());
        }

        @AfterEach
        void tearDown() {
            mongoTemplate.getCollection("datos_sensores").deleteMany(new Document());
            ubicacionService.eliminarTodos();
            registroHistoricoService.reiniciarBaseDeDatos();
        }

        private void insertSonido(String ubicacion, double decibeles) {
            mongoTemplate.getCollection("datos_sensores")
                    .insertOne(
                            new Document()
                                    .append("ubicacion", ubicacion)
                                    .append("sensor_id", 1)
                                    .append("decibeles", decibeles)
                    );
        }

        private void insertTemperatura(String ubicacion, double grados) {
            mongoTemplate.getCollection("datos_sensores")
                    .insertOne(
                            new Document()
                                    .append("ubicacion", ubicacion)
                                    .append("sensor_id", 1)
                                    .append("temperatura",
                                            new Document()
                                                    .append("grados", grados)
                                                    .append("unidad", "°C"))
                    );
        }

        private void insertPresion(String ubicacion, double hectopascales) {
            mongoTemplate.getCollection("datos_sensores")
                    .insertOne(
                            new Document()
                                    .append("ubicacion", ubicacion)
                                    .append("sensor_id", 1)
                                    .append("presionAtmosferica", hectopascales)
                    );
        }

        private void insertLectura(String ubicacion, String lectura) {
            mongoTemplate.getCollection("datos_sensores")
                    .insertOne(
                            new Document()
                                    .append("ubicacion", ubicacion)
                                    .append("sensor_id", 1)
                                    .append("l3kTUr4", lectura)
                    );
        }

        @Test
        void sucesosInsertandoDocsSonido() {
            insertSonido("A", 80);
            insertSonido("A", 83);
            insertSonido("A", 87);
            insertSonido("A", 92);
            insertSonido("A", 105);

            Map<String, Integer> map = ubicacionService.sucesos();

            assertEquals(2, map.get("exorcismos"));
            assertEquals(0, map.get("invocaciones demoniacas"));
            assertEquals(0, map.get("sucesos desconocidos"));
        }

        @Test
        void sucesosInsertandoDoscTemperatura() {
            insertTemperatura("A", 2);
            insertTemperatura("A", 4);
            insertTemperatura("A", 6);
            insertTemperatura("A", 8);
            insertTemperatura("A", 10);

            Map<String, Integer> map = ubicacionService.sucesos();

            assertEquals(0, map.get("exorcismos"));
            assertEquals(2, map.get("invocaciones demoniacas"));
            assertEquals(0, map.get("sucesos desconocidos"));
        }

        @Test
        void sucesosInsertandoDoscPresion() {
            insertPresion("A", 940);
            insertPresion("A", 960);
            insertPresion("A", 980);
            insertPresion("A", 1000);
            insertPresion("A", 1020);

            Map<String, Integer> map = ubicacionService.sucesos();

            assertEquals(0, map.get("exorcismos"));
            assertEquals(0, map.get("invocaciones demoniacas"));
            assertEquals(0, map.get("sucesos desconocidos"));
        }

        @Test
        void sucesosInsertandoDoscDesconocidos() {
            insertLectura("A", "aK3mP9qL2rV8nW1oY4zA7bC6dE0fGhIjJ5");
            insertLectura("A", "bK3mP9qL2rV8nW1oY4zA7bC6dE0fGhIjJ5");
            insertLectura("A", "cK3mP9qL2rV8nW1oY4zA7bC6dE0fGhIjJ5");
            insertLectura("A", "dK3mP9qL2rV8nW1oY4zA7bC6dE0fGhIjJ5");
            insertLectura("A", "eK3mP9qL2rV8nW1oY4zA7bC6dE0fGhIjJ5");

            Map<String, Integer> map = ubicacionService.sucesos();

            assertEquals(0, map.get("exorcismos"));
            assertEquals(0, map.get("invocaciones demoniacas"));
            assertEquals(5, map.get("sucesos desconocidos"));
        }

        @Test
        void sucesosInsertandoMultiplesDocumentos() {
            insertSonido("A", 80);
            insertSonido("A", 83);
            insertSonido("A", 87);
            insertSonido("A", 92);
            insertSonido("A", 105);

            insertTemperatura("A", 2);
            insertTemperatura("A", 4);
            insertTemperatura("A", 6);
            insertTemperatura("A", 8);
            insertTemperatura("A", 10);

            insertPresion("A", 940);
            insertPresion("A", 960);
            insertPresion("A", 980);
            insertPresion("A", 1000);
            insertPresion("A", 1020);

            insertLectura("A", "aK3mP9qL2rV8nW1oY4zA7bC6dE0fGhIjJ5");
            insertLectura("A", "bK3mP9qL2rV8nW1oY4zA7bC6dE0fGhIjJ5");
            insertLectura("A", "cK3mP9qL2rV8nW1oY4zA7bC6dE0fGhIjJ5");
            insertLectura("A", "dK3mP9qL2rV8nW1oY4zA7bC6dE0fGhIjJ5");
            insertLectura("A", "eK3mP9qL2rV8nW1oY4zA7bC6dE0fGhIjJ5");

            Map<String, Integer> map = ubicacionService.sucesos();

            assertEquals(2, map.get("exorcismos"));
            assertEquals(2, map.get("invocaciones demoniacas"));
            assertEquals(5, map.get("sucesos desconocidos"));
        }

    }

    // --------------------------------------------------------------------------------
    // MONGO: ubicacionesConflictivas()
    // --------------------------------------------------------------------------------

    @Nested
    class UbicacionesConflictivas {
        private static final Integer UMBRAL = 90;
        @Autowired
        private MongoTemplate mongoTemplate;

        @BeforeEach
        void setUp() {
            mongoTemplate.getCollection("datos_sensores").deleteMany(new Document());
        }

        @AfterEach
        void tearDown() {
            mongoTemplate.getCollection("datos_sensores").deleteMany(new Document());
            ubicacionService.eliminarTodos();
            registroHistoricoService.reiniciarBaseDeDatos();
        }

        private void insertSonido(String ubicacion, double decibeles) {
            mongoTemplate.getCollection("datos_sensores")
                    .insertOne(
                            new Document()
                                    .append("ubicacion", ubicacion)
                                    .append("sensor_id", 1)
                                    .append("decibeles", decibeles)
                    );
        }

        private void insertTemperatura(String ubicacion, double grados) {
            mongoTemplate.getCollection("datos_sensores")
                    .insertOne(
                            new Document()
                                    .append("ubicacion", ubicacion)
                                    .append("sensor_id", 1)
                                    .append("temperatura",
                                            new Document()
                                                    .append("grados", grados)
                                                    .append("unidad", "°C"))
                    );
        }


        // ------------------------------------------------------------
        // CASO 1: retorna ubicaciones con decibeles altos
        // ------------------------------------------------------------

        @Test
        void devuelveUbicacionesConExorcismos() {
            insertSonido("A", 100);
            insertSonido("A", 110);
            insertSonido("B", 95);

            List<String> resultado = ubicacionService.ubicacionesConflictivas();

            assertEquals(2, resultado.size());
            assertTrue(resultado.contains("A"));
            assertTrue(resultado.contains("B"));
        }

        // ------------------------------------------------------------
        // CASO 2: elimina duplicados
        // ------------------------------------------------------------

        @Test
        void noDevuelveUbicacionesDuplicadas() {
            insertSonido("A", 100);
            insertSonido("A", 110);

            List<String> resultado = ubicacionService.ubicacionesConflictivas();

            assertEquals(1, resultado.size());
            assertEquals("A", resultado.getFirst());
        }

        // ------------------------------------------------------------
        // CASO 3: ignora sonidos por debajo del umbral
        // ------------------------------------------------------------

        @Test
        void ignoraDecibelesBajos() {
            insertSonido("A", 50);
            insertSonido("B", 89);
            insertSonido("C", UMBRAL);

            List<String> resultado = ubicacionService.ubicacionesConflictivas();

            assertTrue(resultado.isEmpty());
        }

        // ------------------------------------------------------------
        // CASO 4: ignora otros tipos de sensores (temperatura)
        // ------------------------------------------------------------

        @Test
        void ignoraDocumentosQueNoSonSonido() {
            insertTemperatura("A", 10);
            insertTemperatura("B", 10);

            List<String> resultado = ubicacionService.ubicacionesConflictivas();

            assertTrue(resultado.isEmpty());
        }

        // ------------------------------------------------------------
        // CASO 5: mezcla de datos válidos e inválidos
        // ------------------------------------------------------------

        @Test
        void mezclaDeDatosSoloDevuelveValidos() {
            insertSonido("A", 120); // VALIDO
            insertTemperatura("A", 10);
            insertSonido("B", 30);
            insertSonido("C", 200); // VALIDO

            List<String> resultado = ubicacionService.ubicacionesConflictivas();

            assertEquals(2, resultado.size());
            assertTrue(resultado.contains("A"));
            assertTrue(resultado.contains("C"));
        }
    }

    // --------------------------------------------------------------------------------
    // MONGO: normalize()
    // --------------------------------------------------------------------------------

    @Nested
    class Normalize {
        private static final double TOPE_DECIBELES = 90.0;
        private static final double TOPE_TEMPERATURA = 5.0;
        @Autowired
        private MongoTemplate mongoTemplate;

        @BeforeEach
        void setUp() {
            mongoTemplate.getCollection("datos_sensores").deleteMany(new Document());
            mongoTemplate.getCollection("datos_normalizados").deleteMany(new Document());
        }

        @AfterEach
        void tearDown() {
            mongoTemplate.getCollection("datos_sensores").deleteMany(new Document());
            mongoTemplate.getCollection("datos_normalizados").deleteMany(new Document());
            ubicacionService.eliminarTodos();
            registroHistoricoService.reiniciarBaseDeDatos();
        }

        private void insertSonido(String ubicacion, double decibeles) {
            mongoTemplate.getCollection("datos_sensores").insertOne(
                    new Document()
                            .append("ubicacion", ubicacion)
                            .append("sensor_id", 12)
                            .append("decibeles", decibeles)
                            .append("fecha", "2025-04-10T21:30:00Z")
            );
        }

        private void insertTemperatura(String ubicacion, double grados) {
            mongoTemplate.getCollection("datos_sensores").insertOne(
                    new Document()
                            .append("ubicacion", ubicacion)
                            .append("sensor_id", 5)
                            .append("temperatura", new Document()
                                    .append("grados", grados)
                                    .append("unidad", "°C"))
                            .append("fecha", "2025-04-10T21:30:00Z")
            );
        }

        private void insertPresion(String ubicacion, double hectopascales) {
            mongoTemplate.getCollection("datos_sensores").insertOne(
                    new Document()
                            .append("ubicacion", ubicacion)
                            .append("sensor_id", 17)
                            .append("presionAtmosferica", hectopascales)
                            .append("fecha", "2025-04-10T21:30:00Z")
            );
        }

        private void insertLectura(String ubicacion, String lectura) {
            mongoTemplate.getCollection("datos_sensores").insertOne(
                    new Document()
                            .append("ubicacion", ubicacion)
                            .append("sensor_id", null)
                            .append("l3kTUr4", lectura)
                            .append("fecha", "2025-04-10T21:30:00Z")
            );
        }

        // ------------------------------------------------------------
        // CASO 1: Documento de sonido que supera el umbral (Exorcismo)
        // ------------------------------------------------------------
        @Test
        void normalizaDocumentoDeSonidoComoExorcismoCorrectamente() {
            insertSonido("Cementerio", 97.4); // > 90.0

            ubicacionService.normalize();

            Document doc = mongoTemplate.getCollection("datos_normalizados").find().first();
            assertNotNull(doc);
            assertEquals("Cementerio", doc.getString("ubicacion"));
            assertEquals(12, doc.getInteger("sensor_id"));
            assertEquals("sonido", doc.getString("medicion"));
            assertEquals("Exorcismo", doc.getString("evento"));
            assertEquals("2025-04-10T21:30:00Z", doc.get("fecha"));

            Document resultadoSensor = (Document) doc.get("resultado_sensor");
            assertNotNull(resultadoSensor);
            assertEquals(97.4, resultadoSensor.getDouble("valor"), 0.01);
            assertEquals("dB", resultadoSensor.getString("medida"));
        }

        // ------------------------------------------------------------
        // CASO 2: Documento de temperatura bajo el umbral (Invocación)
        // ------------------------------------------------------------
        @Test
        void normalizaDocumentoDeTemperaturaComoInvocacionDemoniaca() {
            insertTemperatura("Catedral Oscura", 2.0); // < 5.0

            ubicacionService.normalize();

            Document doc = mongoTemplate.getCollection("datos_normalizados").find().first();
            assertNotNull(doc);
            assertEquals("temperatura", doc.getString("medicion"));
            assertEquals("Invocacion", doc.getString("evento"));

            Document resultadoSensor = (Document) doc.get("resultado_sensor");
            assertEquals(2.0, resultadoSensor.getDouble("valor"), 0.01);
            assertEquals("°C", resultadoSensor.getString("medida"));
        }

        // ------------------------------------------------------------
        // CASO 3: Documento de presión atmosférica (Presencia)
        // ------------------------------------------------------------
        @Test
        void normalizaDocumentoDePresionComoUbicacionDetectada() {
            insertPresion("Bosque Arcano", 1100.0);

            ubicacionService.normalize();

            Document doc = mongoTemplate.getCollection("datos_normalizados").find().first();
            assertNotNull(doc);
            assertEquals("presión", doc.getString("medicion"));
            assertEquals("Ubicacion detectada", doc.getString("evento"));

            Document resultadoSensor = (Document) doc.get("resultado_sensor");
            assertEquals(1100.0, resultadoSensor.getDouble("valor"), 0.01);
            assertEquals("hPa", resultadoSensor.getString("medida"));
        }

        // ------------------------------------------------------------
        // CASO 4: Documento corrupto o desconocido (Anomalía)
        // ------------------------------------------------------------
        @Test
        void normalizaDocumentoDesconocidoComoSucesoDesconocido() {
            insertLectura("Ruinas del Sur", "xK3mP9qL2rV8nW1oY4zA7bC6dE0fGhIjJ5");

            ubicacionService.normalize();

            Document doc = mongoTemplate.getCollection("datos_normalizados").find().first();
            assertNotNull(doc);
            assertNull(doc.get("sensor_id"));
            assertEquals("desconocido", doc.getString("medicion"));
            assertEquals("Desconocida", doc.getString("evento"));

            Document resultadoSensor = (Document) doc.get("resultado_sensor");
            assertNull(resultadoSensor.get("valor"));
            assertEquals("N/A", resultadoSensor.getString("medida"));
        }

        // ------------------------------------------------------------
        // CASO 5: Simulación de lote completo (Contador total)
        // ------------------------------------------------------------
        @Test
        void alNormalizarMultiplesDocumentosLaCantidadFinalCoincide() {
            insertSonido("A", 95.0);
            insertTemperatura("B", 1.0);
            insertPresion("C", 1020.0);
            insertLectura("D", "lecturaEncriptada");

            ubicacionService.normalize();

            long totalOriginales = mongoTemplate.getCollection("datos_sensores").countDocuments();
            long totalNormalizados = mongoTemplate.getCollection("datos_normalizados").countDocuments();

            assertEquals(4, totalOriginales);
            assertEquals(totalOriginales, totalNormalizados);
        }
    }

}
