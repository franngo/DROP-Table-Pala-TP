package ar.edu.unq.epersgeist.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EspirituTest {

    Espiritu espirituM;

    @BeforeEach
    void setUp() {
        espirituM = new Espiritu("Pokemon", "Matias");
    }

    @Test
    public void unEspirituSeCreaConNivelDeConexionEnCero() {


        assertEquals(0, espirituM.getNivelDeConexion());
    }

    @Test
    public void aumentarConexionIncrementaElNivelEnDiez() {

        Espiritu espiritu = new Espiritu("Pokemon", "Matias");
        Medium medium = new Medium("Juan", 100, 10);

        espiritu.aumentarConexion(medium);

        assertEquals(10, espiritu.getNivelDeConexion());
    }

    @Test
    public void aumentarConexionSeAcumulaCorrectamente() {


        Medium medium = new Medium("Juan", 100, 10);

        espirituM.aumentarConexion(medium);
        espirituM.aumentarConexion(medium);

        assertEquals(20, espirituM.getNivelDeConexion());
    }

    @Test
    public void alAumentarConexionElMediumRecibeElEspiritu() {


        Medium medium = new Medium("Juan", 100, 10);

        espirituM.aumentarConexion(medium);

        assertTrue(medium.getEspiritus().contains(espirituM));
    }

    @Test
    public void fallaSiElMediumEsNull() {


        assertThrows(RuntimeException.class, () -> {
            espirituM.aumentarConexion(null);
        });
    }
}
