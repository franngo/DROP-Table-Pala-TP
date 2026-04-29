package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlConectarEspirituConMedium;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EspirituTest {

    Espiritu espirituM;
    Medium medium;
    Ubicacion ubicacion;

    @BeforeEach
    void setUp() {
        ubicacion = new Ubicacion("Berazategui");
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

        medium = new Medium("Juan", 100, 100000, ubicacion);
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
        Ubicacion otraUbicacion = new Ubicacion("Quilmes");
        Medium otroMedium = new Medium("Juan2", 100, 50, otraUbicacion);

        // Intento conectar al angel con otroMedium, sin estar estos en la misma Ubicacion, y fallo
        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> espirituM.aumentarConexionYConectar(otroMedium));
    }
}
