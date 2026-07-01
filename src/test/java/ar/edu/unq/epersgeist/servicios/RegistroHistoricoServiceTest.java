package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RegistroHistoricoServiceTest {

    @Autowired
    private RegistroHistoricoService registroHistoricoService;

    private Espiritu espirituAngelical;
    private Espiritu espirituDemonio;
    private Ubicacion ubicacionSantuario;
    private String ravenIdAngel;
    private String ravenIdDemonio;

    @BeforeEach
    void setUp() {
        ubicacionSantuario = new Santuario("Santuario Test", 100);
        ubicacionSantuario.setId(1L);

        espirituAngelical = new Angelical("Matias", ubicacionSantuario);
        espirituAngelical.setId(10L);
        ravenIdAngel = "espiritus_sql/" + espirituAngelical.getId();

        espirituDemonio = new Demonio("Azazel", ubicacionSantuario);
        espirituDemonio.setId(20L);
        ravenIdDemonio = "espiritus_sql/" + espirituDemonio.getId();
    }

    @AfterEach
    void tearDown() {
        registroHistoricoService.reiniciarBaseDeDatos();
    }

    // --------------------------------------------------------------------------------
    // REGISTRAR ESTADO / OBTENER HISTORIAL
    // --------------------------------------------------------------------------------

    @Nested
    class RegistrarYObtenerHistorial {

        @Test
        void seRegistraUnEstadoCorrectamenteYSePuedeRecuperar() {
            registroHistoricoService.registrarEstado(ravenIdAngel, espirituAngelical);

            List<Espiritu> historial = registroHistoricoService.obtenerHistorial(Espiritu.class, ravenIdAngel);

            assertFalse(historial.isEmpty());
            assertEquals(1, historial.size());
            assertEquals("Matias", historial.getFirst().getNombre());
        }

        @Test
        void registrarMultiplesEstadosConstruyeUnHistorialCronologico() {
            registroHistoricoService.registrarEstado(ravenIdAngel, espirituAngelical);

            // Simulamos una mutación ordinaria (Nivel de conexión)
            espirituAngelical.setNivelDeConexion(50);
            registroHistoricoService.registrarEstado(ravenIdAngel, espirituAngelical);

            List<Espiritu> historial = registroHistoricoService.obtenerHistorial(Espiritu.class, ravenIdAngel);

            assertEquals(2, historial.size());
            assertEquals(0, historial.get(0).getNivelDeConexion());
            assertEquals(50, historial.get(1).getNivelDeConexion());
        }

        @Test
        void obtenerHistorialDeUnIdInexistenteDevuelveListaVacia() {
            List<Espiritu> historial = registroHistoricoService.obtenerHistorial(Espiritu.class, "id_totalmente_inexistente");

            assertTrue(historial.isEmpty());
        }
    }

    // --------------------------------------------------------------------------------
    // OBTENER ESTADO EN FECHA (AUDITORÍA TEMPORAL)
    // --------------------------------------------------------------------------------

    @Nested
    class ObtenerEstadoEnFecha {

        @Test
        void seObtieneElEstadoExactoEnUnaFechaEspecifica() throws Exception {
            // Estado 1: El espíritu nace libre y sin conexión
            registroHistoricoService.registrarEstado(ravenIdAngel, espirituAngelical);
            LocalDateTime fechaEstadoInicial = LocalDateTime.now();

            Thread.sleep(1000); // Pausa para distanciar los snapshots temporales

            // Estado 2: Modificamos el bando o atributos
            espirituAngelical.setNivelDeConexion(99);
            registroHistoricoService.registrarEstado(ravenIdAngel, espirituAngelical);
            LocalDateTime fechaEstadoFinal = LocalDateTime.now();

            // Auditorías temporales
            Espiritu enElPasado = registroHistoricoService.obtenerEstadoEnFecha(Espiritu.class, ravenIdAngel, fechaEstadoInicial);
            Espiritu enElPresente = registroHistoricoService.obtenerEstadoEnFecha(Espiritu.class, ravenIdAngel, fechaEstadoFinal);

            assertEquals(0, enElPasado.getNivelDeConexion());
            assertEquals(99, enElPresente.getNivelDeConexion());
        }

        @Test
        void buscarEstadoEnUnaFechaPreviaACreacionDevuelveNull() {
            LocalDateTime haceUnaHora = LocalDateTime.now().minusHours(1);
            registroHistoricoService.registrarEstado(ravenIdAngel, espirituAngelical);

            Espiritu resultado = registroHistoricoService.obtenerEstadoEnFecha(Espiritu.class, ravenIdAngel, haceUnaHora);

            assertNull(resultado);
        }

        @Test
        void buscarEstadoEnElFuturoDevuelveElUltimoEstadoConocido() {
            registroHistoricoService.registrarEstado(ravenIdAngel, espirituAngelical);
            LocalDateTime dentroDeUnAnio = LocalDateTime.now().plusYears(1);

            Espiritu resultado = registroHistoricoService.obtenerEstadoEnFecha(Espiritu.class, ravenIdAngel, dentroDeUnAnio);

            assertNotNull(resultado);
            assertEquals("Matias", resultado.getNombre());
        }
    }

    // --------------------------------------------------------------------------------
    // OBTENER TODOS LOS IDS DE UNA COLECCIÓN
    // --------------------------------------------------------------------------------

    @Nested
    class ObtenerTodosLosIdsDeColeccion {

        @Test
        void seRecuperanLosIdsCorrectamenteFiltradosPorSuPrefijoOColeccion() {
            registroHistoricoService.registrarEstado(ravenIdAngel, espirituAngelical);
            registroHistoricoService.registrarEstado(ravenIdDemonio, espirituDemonio);

            // Registramos un objeto de otra colección para validar el filtrado lof
            registroHistoricoService.registrarEstado("ubicaciones_sql/1", ubicacionSantuario);

            List<String> idsEspiritus = registroHistoricoService.obtenerTodosLosIdsDeColeccion("espiritus_sql");

            assertEquals(2, idsEspiritus.size());
            assertTrue(idsEspiritus.contains(ravenIdAngel));
            assertTrue(idsEspiritus.contains(ravenIdDemonio));
            assertFalse(idsEspiritus.contains("ubicaciones_sql/1"));
        }

        @Test
        void buscarIdsDeColeccionInexistenteDevuelveListaVacia() {
            List<String> ids = registroHistoricoService.obtenerTodosLosIdsDeColeccion("coleccion_fantasma");

            assertTrue(ids.isEmpty());
        }
    }

    // --------------------------------------------------------------------------------
    // REINICIAR BASE DE DATOS
    // --------------------------------------------------------------------------------

    @Nested
    class ReiniciarBaseDeDatos {

        @Test
        void reiniciarBaseDeDatosLimpiaTodoElHistorialDeRaven() {
            registroHistoricoService.registrarEstado(ravenIdAngel, espirituAngelical);
            registroHistoricoService.registrarEstado(ravenIdDemonio, espirituDemonio);

            // Validamos que existan datos antes del reset
            assertFalse(registroHistoricoService.obtenerHistorial(Espiritu.class, ravenIdAngel).isEmpty());

            // Ejecutamos limpieza de base políglota temporal
            registroHistoricoService.reiniciarBaseDeDatos();

            // Verificamos que todo quedó en 0 / vacío
            assertTrue(registroHistoricoService.obtenerHistorial(Espiritu.class, ravenIdAngel).isEmpty());
            assertTrue(registroHistoricoService.obtenerTodosLosIdsDeColeccion("espiritus_sql").isEmpty());
        }
    }
}