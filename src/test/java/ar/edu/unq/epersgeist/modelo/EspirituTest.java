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

    @BeforeEach
    void setUp() {

        espirituM = new Angelical("Matias");
        espiritu = new Angelical("Matias");
        medium = new Medium("Juan", 100, 10);
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

        medium = new Medium("Juan", 10000, 10);

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

        medium = new Medium("Juan", 100, 100000);
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
        Medium medium = new Medium("Juan", 100, 50);
        Angelical angel = new Angelical("Tyrael");
        medium.agregarEspiritu(angel);

        assertNotNull(angel.getMediumCon());

        angel.desvincularse();
        assertTrue(angel.estaLibre());
        // FALLA
        assertNull(angel.getMediumCon(),
                "Después de desvincularse, mediumCon debe ser null");
    }


}
