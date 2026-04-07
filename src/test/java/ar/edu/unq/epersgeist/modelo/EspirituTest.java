package ar.edu.unq.epersgeist.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EspirituTest {

    Espiritu espirituM;
    Espiritu espiritu;
    Medium medium;

    @BeforeEach
    void setUp() {

        espirituM = new Espiritu("Pokemon", "Matias");
        espiritu = new Espiritu("Pokemon", "Matias");
        medium = new Medium("Juan", 100, 10);
    }

    @Test
    public void unEspirituSeCreaConNivelDeConexionEnCero() {


        assertEquals(0, espirituM.getNivelDeConexion());
    }

    @Test
    public void aumentarConexionIncrementaElNivelEnDiez() {



        espiritu.aumentarConexion(medium);

        assertEquals(10, espiritu.getNivelDeConexion());
    }

    @Test
    public void aumentarConexionSeAcumulaCorrectamente() {



        espirituM.aumentarConexion(medium);
        espirituM.aumentarConexion(medium);

        assertEquals(20, espirituM.getNivelDeConexion());
    }

    @Test
    public void alAumentarConexionElMediumRecibeElEspiritu() {



        espirituM.aumentarConexion(medium);

        assertTrue(medium.getEspiritus().contains(espirituM));
    }

    @Test
    public void alAumentarMuchaConexionNoDebePasarDeCien() {


        espirituM.fortalecerConexion(99);
        assertEquals(99, espirituM.getNivelDeConexion());
        espirituM.fortalecerConexion(100);
        assertEquals(99, espirituM.getNivelDeConexion());

    }

    @Test
    public void fallaSiElMediumEsNull() {

        assertThrows(RuntimeException.class, () -> {
            espirituM.aumentarConexion(null);
        });
    }
}
