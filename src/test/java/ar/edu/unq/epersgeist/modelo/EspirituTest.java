package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EspirituTest {

    Espiritu espirituM;
    Espiritu espiritu;
    Medium medium;
    Ubicacion ubicacion;

    @BeforeEach
    void setUp() {
        ubicacion = new Ubicacion("Berazategui");
        espirituM = new Angelical("Matias", ubicacion);
        espiritu = new Angelical("Matias", ubicacion);
        medium = new Medium("Juan", 100, 10, ubicacion);
    }

    @Test
    public void unEspirituSeCreaConNivelDeConexionEnCero() {

        assertEquals(0, espirituM.getNivelDeConexion());
    }

    @Test
    public void seAumentaLaConexionEnUn20PorCientoDelManaDelMedium() {

        espiritu.aumentarConexionYConectar(medium);

        assertEquals(2, espiritu.getNivelDeConexion());
    }

    @Test
    public void alAumentarConexionMasDeUnaVezLosNivelesDeConexionSeSuman() {

        medium = new Medium("Juan", 10000, 10, ubicacion);

        espirituM.aumentarConexionYConectar(medium);
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
        Medium medium = new Medium("Juan", 100, 50, ubicacion);
        Angelical angel = new Angelical("Tyrael", ubicacion);
        medium.agregarEspiritu(angel);

        assertNotNull(angel.getMediumCon());

        angel.desvincularse();
        assertTrue(angel.estaLibre());
        // FALLA
        assertNull(angel.getMediumCon(),
                "Después de desvincularse, mediumCon debe ser null");
    }


}
