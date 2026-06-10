package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Cementerio;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlConectarEspirituConMedium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDominacionInvalidaCandidatoLoDominaException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDominacionInvalidaCandidatoNoAptoException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDominacionInvalidaPorDistanciaException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EspirituTest {

    private static final Integer ENERGIA = 100;
    Espiritu espirituM;
    Medium medium;
    Ubicacion ubicacion;

    @BeforeEach
    void setUp() {
        ubicacion = new Santuario("Berazategui", ENERGIA);
        espirituM = new Angelical("Matias", ubicacion);
        medium = new Medium("Juan", 100, 10, ubicacion);
    }

    @Test
    public void unEspirituSeCreaConNivelDeConexionEnCero() {

        assertEquals(0, espirituM.getNivelDeConexion());
    }

    @Test
    public void seAumentaLaConexionEnUn20PorCientoDelManaDelMedium() {

        espirituM.aumentarConexionYConectar(medium);

        assertEquals(2, espirituM.getNivelDeConexion());
    }

    @Test
    public void alAumentarConexionMasDeUnaVezLosNivelesDeConexionSeSuman() {

        medium = new Medium("Juan", 10000, 10, ubicacion);

        espirituM.aumentarConexionYConectar(medium);
        espirituM.desvincularse();
        espirituM.aumentarConexionYConectar(medium);

        assertEquals(4, espirituM.getNivelDeConexion());
    }

    @Test
    public void alAumentarConexionElMediumRecibeElEspiritu() {


        espirituM.aumentarConexionYConectar(medium);

        assertTrue(medium.getEspiritus().contains(espirituM));
    }

    @Test
    public void laConexionDelEspirituNoSobrepasaLos100Niveles() {

        medium = new Medium("Juan", 100000, 100000, ubicacion);
        espirituM.aumentarConexionYConectar(medium);

        assertEquals(100, espirituM.getNivelDeConexion());

    }

    @Test
    public void fallaSiElMediumEsNull() {

        assertThrows(RuntimeException.class, () -> {
            espirituM.aumentarConexionYConectar(null);
        });
    }

    @Test
    void elEspirituDesvinculadoDebeLiberarMediumCon() {
        medium.agregarEspiritu(espirituM);

        assertNotNull(espirituM.getMediumCon());

        espirituM.desvincularse();

        assertTrue(espirituM.estaLibre());

        assertNull(espirituM.getMediumCon(),
                "Después de desvincularse, mediumCon debe ser null");
    }

    @Test
    void conectarUnEspirituYaConectadoDebeRechazarse() {
        //espirituM y medium tienen la misma ubicacion

        espirituM.aumentarConexionYConectar(medium);

        // primera conexión OK: unAngel está vinculado con unMedium
        assertEquals(espirituM.getMediumCon(), medium);

        Medium otroMedium = new Medium("Juan2", 100, 50, ubicacion);

        // Intento conectar al mismo angel con otroMedium, sin estar libre, y fallo
        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> espirituM.aumentarConexionYConectar(otroMedium));
    }

    @Test
    void conectarUnEspirituEnDiferenteUbicacionAlMediumFalla() {
        Ubicacion otraUbicacion = new Santuario("Quilmes", ENERGIA);
        Medium otroMedium = new Medium("Juan2", 100, 50, otraUbicacion);

        // Intento conectar al angel con otroMedium, sin estar estos en la misma Ubicacion, y fallo
        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> espirituM.aumentarConexionYConectar(otroMedium));
    }

    // --------------------------------------------------------------------------------
    // dominar()
    // --------------------------------------------------------------------------------

    @Nested
    class Dominar {
        private Espiritu pepe;

        @BeforeEach
        void setUp() {
//            Ya partimos de:
//            ubicacion = new Santuario("Berazategui", ENERGIA);
//            espirituM = new Angelical("Matias", ubicacion); (nivel de conexión: 0)
//            medium = new Medium("Juan", 100, 10, ubicacion);

            pepe = new Angelical("Pepe", ubicacion); //(nivel de conexión: 0)
        }

        @Test
        void fallaCuandoLaDistanciaEsMenorA2000() {
            assertThrows(ErrorDominacionInvalidaPorDistanciaException.class, () -> espirituM.dominar(pepe, 1000.0));
        }

        @Test
        void fallaCuandoLaDistanciaEsMayorA5000() {
            assertThrows(ErrorDominacionInvalidaPorDistanciaException.class, () -> espirituM.dominar(pepe, 6000.0));
        }

        @Test
        void fallaCuandoElObjetivoNoEstaLibre() {
            pepe.aumentarConexionYConectar(medium);
            //pepe ya no está libre, por lo que ya no es apto para ser dominado.

            assertThrows(ErrorDominacionInvalidaCandidatoNoAptoException.class, () -> espirituM.dominar(pepe, 3000.0));
        }

        @Test
        void fallaCuandoElObjetivoNoTieneMenosDeCincuentaDeNivelDeConexion() {
            pepe.setNivelDeConexion(60);
            //en este punto, pepe tiene un nivel de energía tal que no es apto para ser dominado.
            assertThrows(ErrorDominacionInvalidaCandidatoNoAptoException.class, () -> espirituM.dominar(pepe, 3000.0));
        }

        @Test
        void fallaCuandoElObjetivoYaEstaDominado() {
            espirituM.dominar(pepe, 3000.0);
            //en este punto, pepe ya está dominado y no puede ser dominado nuevamente por un Espiritu (sea cual sea).
            assertThrows(ErrorDominacionInvalidaCandidatoNoAptoException.class, () -> espirituM.dominar(pepe, 3000.0));
        }

        @Test
        void casoExitoso() {
            espirituM.dominar(pepe, 3000.0);

            assertTrue(pepe.estaDominado());
            assertEquals(pepe.getEspirituDominante(), espirituM);
        }

    }



}
