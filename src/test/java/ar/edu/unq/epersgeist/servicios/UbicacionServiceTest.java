package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.HarmonicResult;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Cementerio;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionesNoConectadasException;
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
            origen = ubicacionService.crear(new Santuario("ORIGEN", 50));
            destino = ubicacionService.crear(new Cementerio("DESTINO", 50));
            otroNodo = ubicacionService.crear(new Santuario("OTRO", 50));
        }

        @AfterEach
        void tearDown() {
            ubicacionService.eliminarTodos();
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
            origen = ubicacionService.crear(new Santuario("ORIGEN", 50));
            destino = ubicacionService.crear(new Cementerio("DESTINO", 50));
            otroNodo = ubicacionService.crear(new Santuario("OTRO", 50));
        }

        @AfterEach
        void tearDown() {
            ubicacionService.eliminarTodos();
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
            assertEquals(origen.getId(), ubis.get(0).getId());
        }

        @Test
        void caminoMasRentableCasoUbicacionesNoConectadas() {
            //Las Ubicaciones origen y destino NO están conectadas, por lo que solicitar el camino más rentable entre estas dos dará error
            assertThrows(UbicacionesNoConectadasException.class, () -> ubicacionService.caminoMasRentable(origen.getId(), destino.getId()));
        }

        @Test
        void caminoMasRentableCasoExitoso() {
            Ubicacion nodoCuatro = ubicacionService.crear(new Santuario("Nodo cuatro", 50));

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
            Ubicacion nodoCuatro = ubicacionService.crear(new Santuario("Nodo cuatro", 50));

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

            a = ubicacionService.crear(new Santuario("A", 50));
            b = ubicacionService.crear(new Santuario("B", 50));
            c = ubicacionService.crear(new Santuario("C", 50));
            d = ubicacionService.crear(new Santuario("D", 50));

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
                    new Santuario("SIN_CONEXIONES", 50)
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
    class UbicacionSobrecargada{

        @BeforeEach
        void setUp() {
            ubicacionService.crear(new Santuario("Alta energia", 70));
            ubicacionService.crear(new Cementerio("Media energia", 50));
            ubicacionService.crear(new Santuario("Baja energia", 20));
        }

        @AfterEach
        void tearDown() {
            ubicacionService.eliminarTodos();
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
            ubicacionService.crear(new Santuario("Primera", 90));
            ubicacionService.crear(new Cementerio("Segunda", 95));
            List<Ubicacion> sobrecargadas = ubicacionService.ubicacionesSobrecargadas(80);

            assertEquals(2, sobrecargadas.size());
        }

        @Test
        void noDevuelveUbicacionesEliminadas() {
            Ubicacion alta = ubicacionService.crear(new Santuario("Mucha energia", 90));
            ubicacionService.eliminar(alta.getId());

            List<Ubicacion> sobrecargadas = ubicacionService.ubicacionesSobrecargadas(80);

            assertTrue(sobrecargadas.isEmpty());
        }


        @Test
        void elUmbralEsExclusivoNoIncluyeIguales() {
            ubicacionService.crear(new Santuario("Exacto", 50));

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
            uno = ubicacionService.crear(new Santuario("uno", 50));
            dos = ubicacionService.crear(new Cementerio("dos", 50));
            tres = ubicacionService.crear(new Santuario("tres", 50));
            cuatro = ubicacionService.crear(new Santuario("cuatro", 50));
            cinco = ubicacionService.crear(new Cementerio("cinco", 50));
            seis = ubicacionService.crear(new Santuario("seis", 50));
            siete = ubicacionService.crear(new Santuario("siete", 50));
        }

        @AfterEach
        void tearDown() {
            ubicacionService.eliminarTodos();
        }

        /*
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
            assertEquals(origen.getId(), ubis.get(0).getId());
        }

        @Test
        void caminoMasRentableCasoUbicacionesNoConectadas() {
            //Las Ubicaciones origen y destino NO están conectadas, por lo que solicitar el camino más rentable entre estas dos dará error
            assertThrows(UbicacionesNoConectadasException.class, () -> ubicacionService.caminoMasRentable(origen.getId(), destino.getId()));
        }

         */

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
             */

            List<HarmonicResult> ubis = ubicacionService.harmonicResult(List.of(dos.getId(), uno.getId()));

            HarmonicResult unoHR = ubis.get(0);
            HarmonicResult dosHR = ubis.get(1);

            assertEquals(uno.getId(), unoHR.id());
            assertEquals(4.333, unoHR.centrality());

            assertEquals(dos.getId(), dosHR.id());
            assertEquals(4.083, dosHR.centrality());

        }
    }

}
