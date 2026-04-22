package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.exceptions.ExorcistaSinAngelesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MediumTest {

    Medium mediumM;
    Medium mediumM2;
    Espiritu espirituM;
    Espiritu espirituM2;

    // para los test de exorcismo
    Angelical rika;
    Angelical ivaar;
    Angelical hana;
    Demonio jaeger;
    Demonio noroi;


    @BeforeEach
    void setUp() {
        mediumM = new Medium("Juan", 100, 50);
        mediumM2 = new Medium("Matias", 80, 40);
        espirituM = new Espiritu("Fuego", "Charizard");
        espirituM2 = new Espiritu("Agua", "Bulbasor");


        //contexto de los espiritus y demonios en el exorcismo
        rika = new Angelical("Angel", "Rika");
        rika.setNivelDeConexion(60);
        ivaar = new Angelical("Angel", "Ivaar");
        ivaar.setNivelDeConexion(80);
        hana = new Angelical("Angel", "Hana");
        hana.setNivelDeConexion(5);
        jaeger = new Demonio("Demonio", "Jaeger");
        jaeger.setNivelDeConexion(50);
        noroi = new Demonio("Demonio", "Noroi");
        noroi.setNivelDeConexion(66);
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


    //Test de exorcismo

    @Test
    void exorcismoDeberiaSeguirFlujoCorrecto() throws ExorcistaSinAngelesException {


        mediumM.agregarEspiritu(rika);
        mediumM.agregarEspiritu(ivaar);
        mediumM.agregarEspiritu(hana);

        // Demonios


        mediumM2.agregarEspiritu(jaeger);
        mediumM2.agregarEspiritu(noroi);


        mediumM.exorcizar(mediumM2);

        // 🔍 Verificaciones

        // Jaeger muere
        assertTrue(jaeger.getLibre());
        assertEquals(0, jaeger.getNivelDeConexion());

        // Noroi sigue vivo
        assertFalse(noroi.getLibre());
        assertEquals(66, noroi.getNivelDeConexion());

        // Hana es libre
        assertFalse(hana.getLibre());
        assertEquals(0, hana.getNivelDeConexion());
    }
    /*
    @Test
    void deberiaGanarAtacanteSiNoQuedanDemonios() {

        Angelical angel = new Angelical("Rika", 100);
        Demonio demonio = new Demonio("Debil", 10);

        Medium atacante = new Medium();
        atacante.agregarEspiritu(angel);

        Medium atacado = new Medium();
        atacado.agregarEspiritu(demonio);

        Queue<Boolean> resultados = new LinkedList<>();
        resultados.add(true); // lo mata

        Batalla batalla = new Batalla(atacante, atacado, new RondaDeCombate(resultados));

        batalla.iniciarBatalla();

        assertTrue(batalla.ganoAtacante());
    }*/

    @Test
    void mediumNoPuedeExorcizarSinAngeles() {


        assertThrows(ExorcistaSinAngelesException.class, () -> {
            mediumM.exorcizar(mediumM2);
        });
    }


}
