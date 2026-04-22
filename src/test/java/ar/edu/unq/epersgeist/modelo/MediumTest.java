package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.exceptions.ExorcistaSinAngelesException;
import ar.edu.unq.epersgeist.modelo.random.Estrategia.EstrategiaRangoEspecifico;
import ar.edu.unq.epersgeist.modelo.random.Random;
import ar.edu.unq.epersgeist.modelo.random.RandomDefensa;
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
        espirituM = new Angelical("Charizard");
        espirituM2 = new Angelical("Bulbasor");


        //contexto de los espiritus y demonios en el exorcismo
        rika = new Angelical("Rika");
        rika.setNivelDeConexion(60);
        ivaar = new Angelical("Ivaar");
        ivaar.setNivelDeConexion(80);
        hana = new Angelical("Hana");
        hana.setNivelDeConexion(5);
        jaeger = new Demonio("Jaeger");
        jaeger.setNivelDeConexion(50);
        noroi = new Demonio("Noroi");
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
    void exorcismoDeberiaSeguirFlujoCorrectoCon1Espiritu() throws ExorcistaSinAngelesException {
        //seteo los valores random para que el porcentaje de ataque siempre le gane al de defensa
        Random.setEstrategiaNumero(new EstrategiaRangoEspecifico(10, 10));
        RandomDefensa.setEstrategiaNumero(new EstrategiaRangoEspecifico(5, 5));

        mediumM.agregarEspiritu(rika); // rika cuenta con 60 de conexion

        // Demonios
        mediumM2.agregarEspiritu(jaeger); // jaeger tiene 50 de conexion
        mediumM.exorcizar(mediumM2);
        // Jaeger recibe daño 30
        assertFalse(jaeger.getLibre());
        assertEquals(20, jaeger.getNivelDeConexion());

    }

    @Test
    void exorcismoDeberiaSeguirFlujoCorrectoCon2Espiritus() throws ExorcistaSinAngelesException {
        Random.setEstrategiaNumero(new EstrategiaRangoEspecifico(10, 10));
        RandomDefensa.setEstrategiaNumero(new EstrategiaRangoEspecifico(5, 5));

        mediumM.agregarEspiritu(rika); // rika cuenta con 60 de conexion
        mediumM.agregarEspiritu(ivaar); // ivaar cuenta con 80 de conexion
        // Demonios
        mediumM2.agregarEspiritu(jaeger); // jaeger tiene 50 de conexion
        mediumM.exorcizar(mediumM2);
        // Jaeger recibe primero 30 de daño y luego recibe 40 de daño, quedando en 0 y libre
        assertTrue(jaeger.getLibre());
        assertEquals(0, jaeger.getNivelDeConexion());

    }

    @Test
    void elMediumPierdeSuAngelSiPierdeLaBatalla() throws ExorcistaSinAngelesException {
        //se inyecta que el porcentaje de defensa siempre sera mayor
        Random.setEstrategiaNumero(new EstrategiaRangoEspecifico(10, 10));
        RandomDefensa.setEstrategiaNumero(new EstrategiaRangoEspecifico(99, 99));

        mediumM.agregarEspiritu(hana);


        mediumM2.agregarEspiritu(jaeger);

        mediumM.exorcizar(mediumM2);

        assertTrue(hana.getLibre());
        assertEquals(0, hana.getNivelDeConexion());


    }

    @Test
    void mediumNoPuedeExorcizarSinAngeles() {


        assertThrows(ExorcistaSinAngelesException.class, () -> {
            mediumM.exorcizar(mediumM2);
        });
    }


}
