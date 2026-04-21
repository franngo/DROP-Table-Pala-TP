package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.random.Estrategia.EstrategiaRangoEspecifico;
import ar.edu.unq.epersgeist.modelo.random.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MediumTest {

    Medium mediumM;
    Medium mediumM2;
    Espiritu espirituM;
    Espiritu espirituM2;


    @BeforeEach
    void setUp() {
        mediumM = new Medium("Juan", 100, 50);
        mediumM2 = new Medium("Matias", 80, 40);
        espirituM = new Espiritu("Fuego", "Charizard");
        espirituM2 = new Espiritu("Agua", "Bulbasor");
    }

    @Test
    public void seCreaMediumConValoresInicialesCorrectos() {

        assertEquals("Juan", mediumM.getNombre());
        assertEquals(100, mediumM.getManaMax());
        assertEquals(50, mediumM.getMana());
        assertNotNull(mediumM.getEspiritus());
        assertTrue(mediumM.getEspiritus().isEmpty());
    }

    @Test
    public void agregarEspirituAgregaUnEspirituALaLista() {

        mediumM.agregarEspiritu(espirituM);

        assertEquals(1, mediumM.getEspiritus().size());
        assertTrue(mediumM.getEspiritus().contains(espirituM));
    }

    @Test
    public void agregarMultiplesEspiritusFuncionaCorrectamente() {

        mediumM.agregarEspiritu(espirituM);
        mediumM.agregarEspiritu(espirituM2);

        assertEquals(2, mediumM.getEspiritus().size());
        assertTrue(mediumM.getEspiritus().contains(espirituM));
        assertTrue(mediumM.getEspiritus().contains(espirituM2));
    }

    @Test
    void excorizar() {
        Random.setEstrategiaNumero(new EstrategiaRangoEspecifico(1, 1));
        assertEquals(1, mediumM.porcentajeDeAtaqueExitoso(espirituM));
    }

}
