package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Cementerio;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDeInvocacionDeEspirituException;
import ar.edu.unq.epersgeist.modelo.exceptions.ExorcistaSinAngelesException;
import ar.edu.unq.epersgeist.modelo.random.Estrategia.EstrategiaRangoEspecifico;
import ar.edu.unq.epersgeist.modelo.random.Random;
import ar.edu.unq.epersgeist.modelo.random.RandomDefensa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MediumTest {
    private static final Integer ENERGIA = 100;

    Ubicacion bernal = new Santuario("Bernal", ENERGIA);


    Ubicacion cementerio;
    Ubicacion santuario;


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

        cementerio = new Cementerio("Cementerio Chacarita", ENERGIA);
        santuario = new Santuario("Santario Lujan", ENERGIA);

        mediumM = new Medium("Juan", 100, 50, bernal);
        mediumM2 = new Medium("Matias", 80, 40, bernal);
        espirituM = new Angelical("Charizard", bernal);
        espirituM2 = new Angelical("Bulbasor", bernal);


        //contexto de los espiritus y demonios en el exorcismo
        rika = new Angelical("Rika", bernal);
        rika.setNivelDeConexion(60);
        ivaar = new Angelical("Ivaar", bernal);
        ivaar.setNivelDeConexion(80);
        hana = new Angelical("Hana", bernal);
        hana.setNivelDeConexion(5);
        jaeger = new Demonio("Jaeger", bernal);
        jaeger.setNivelDeConexion(50);
        noroi = new Demonio("Noroi", bernal);
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

    @Test
    public void descansar() {
        assertEquals(50, mediumM.getMana());
        assertEquals(0, espirituM.getNivelDeConexion());
        assertEquals(0, espirituM2.getNivelDeConexion());

        mediumM.agregarEspiritu(espirituM);
        mediumM.agregarEspiritu(espirituM2);

        mediumM.descansar();

        assertEquals(65, mediumM.getMana());
        assertEquals(5, espirituM.getNivelDeConexion());
        assertEquals(5, espirituM2.getNivelDeConexion());
    }

    @Test
    public void noSeExcedenLimitesEnDescansar() {
        assertEquals(50, mediumM.getMana());
        assertEquals(0, espirituM.getNivelDeConexion());
        assertEquals(0, espirituM2.getNivelDeConexion());

        mediumM.agregarEspiritu(espirituM);
        mediumM.agregarEspiritu(espirituM2);

        mediumM.setMana(89); //tiene un manaMax de 100
        espirituM.setNivelDeConexion(94); //el límite del nivelDeConexion es de 100
        espirituM2.setNivelDeConexion(97); //el límite del nivelDeConexion es de 100

        mediumM.descansar();

        assertEquals(100, mediumM.getMana());
        assertEquals(99, espirituM.getNivelDeConexion());
        assertEquals(100, espirituM2.getNivelDeConexion());
    }

    @Test
    public void invocarCasoExitoso() {
        Ubicacion ubicacion = new Santuario("Quilmes", ENERGIA);
        mediumM.setUbicacion(ubicacion);

        assertEquals(50, mediumM.getMana());


        //mediumM tiene suficiente maná (50) y espirituM está libre

        mediumM.invocar(espirituM);

        assertEquals(40, mediumM.getMana());
        assertEquals(mediumM.getUbicacion(), espirituM.getUbicacion());
    }

    @Test
    public void invocarCasoManaInsuficiente() {
        Ubicacion ubicacion = new Santuario("Quilmes", ENERGIA);
        mediumM.setUbicacion(ubicacion);

        mediumM.setMana(5); //mana insuficiente para invocar()
        Ubicacion ubiActual = espirituM.getUbicacion();
        mediumM.invocar(espirituM);

        //no hay ningún cambio de estado
        assertEquals(5, mediumM.getMana());

        // No cambio la ubicacion del espiritu
        assertEquals(ubiActual.getNombre(), espirituM.getUbicacion().getNombre());
    }

    @Test
    public void invocarCasoFallido() {
        Ubicacion ubicacion = new Santuario("Quilmes", ENERGIA);
        mediumM.setUbicacion(ubicacion);

        espirituM.setMediumCon(mediumM2); //espirituM pasa a NO estar libre, por lo que no puede ser invocado

        assertThrows(ErrorDeInvocacionDeEspirituException.class, () -> mediumM.invocar(espirituM));
    }

    //Test de exorcismo

    @Test
    void exorcismoDeberiaSeguirFlujoCorrectoCon1Espiritu() throws ExorcistaSinAngelesException {
        //seteo los valores random para que el porcentaje de ataque siempre le gane al de defensa
        Random.setEstrategiaNumero(new EstrategiaRangoEspecifico(10, 10));
        RandomDefensa.setEstrategiaNumero(new EstrategiaRangoEspecifico(5, 5));

        mediumM.agregarEspiritu(rika);
        assertEquals(60, rika.getNivelDeConexion());// rika cuenta con 60 de conexion

        // Demonios
        mediumM2.agregarEspiritu(jaeger);
        assertEquals(50, jaeger.getNivelDeConexion());// jaeger tiene 50 de conexion
        mediumM.exorcizar(mediumM2);
        // Jaeger recibe daño 30 pero sigue conectado al medium2

        assertFalse(jaeger.estaLibre());

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
        assertTrue(jaeger.estaLibre());
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

        assertTrue(hana.estaLibre());
        assertEquals(0, hana.getNivelDeConexion());


    }

    @Test
    void mediumNoPuedeExorcizarSinAngeles() {


        assertThrows(ExorcistaSinAngelesException.class, () -> {
            mediumM.exorcizar(mediumM2);
        });
    }

 
    // MOVER
    @Test
    public void elMediumSeMueveExitosamente() {
        //El medium se conecta con dos espiritus y se comprueba que los conoce
        rika.aumentarConexionYConectar(mediumM);
        jaeger.aumentarConexionYConectar(mediumM);

        assertTrue(mediumM.getEspiritus().contains(rika));
        assertTrue(mediumM.getEspiritus().contains(jaeger));

        //todos en la misma ubicacion
        assertEquals(bernal, mediumM.getUbicacion());
        assertEquals(bernal, rika.getUbicacion());
        assertEquals(bernal, jaeger.getUbicacion());

        mediumM.moverA(santuario);

        // todos se movieron al santuario
        assertEquals(santuario, mediumM.getUbicacion());
        assertEquals(santuario, rika.getUbicacion());
        assertEquals(santuario, jaeger.getUbicacion());

    }

    @Test
    public void elMediumSeMueveALaMismaUbicacion() {
        //El medium se conecta con dos espiritus y se comprueba que los conoce
        jaeger.aumentarConexionYConectar(mediumM);
        rika.aumentarConexionYConectar(mediumM);

        assertTrue(mediumM.getEspiritus().contains(rika));
        assertTrue(mediumM.getEspiritus().contains(jaeger));

        //todos en la misma ubicacion
        assertEquals(bernal, mediumM.getUbicacion());
        assertEquals(bernal, rika.getUbicacion());
        assertEquals(bernal, jaeger.getUbicacion());

        mediumM.moverA(bernal);

        //todos se siguen en la misma ubicacion
        assertEquals(bernal, mediumM.getUbicacion());
        assertEquals(bernal, rika.getUbicacion());
        assertEquals(bernal, jaeger.getUbicacion());

    }

    // CONEXION DE EXPIRITUS AL ARRIBAR A SANTUARIOS
    @Test
    public void conexionDemonios_SI_CambiaAlMoverseA_SANTUARIO() {
        //El medium tiene 1 demonio
        jaeger.aumentarConexionYConectar(mediumM);

        //comprobamos conexion del demonio
        assertEquals(60, jaeger.getNivelDeConexion());

        mediumM.moverA(santuario);
        // el demonio pierde 10 unidades
        assertEquals(50, jaeger.getNivelDeConexion());
    }

    @Test
    public void conexionAngeles_NO_CambiaAlMoverseA_SANTUARIO() {
        //El medium tiene 1 angel
        rika.aumentarConexionYConectar(mediumM);

        //comprobamos conexion del angel
        assertEquals(70, rika.getNivelDeConexion());

        mediumM.moverA(santuario);
        // el angel no se ve afectado
        assertEquals(70, rika.getNivelDeConexion());
    }

    // CONEXION DE ESPIRITUS AL ARRIBAR A CEMENTERIOS
    @Test
    public void conexionDemonios_NO_CambiaAlMoverseA_CEMENTERIO() {
        //El medium tiene 1 demonio
        jaeger.aumentarConexionYConectar(mediumM);

        //comprobamos conexion del demonio
        assertEquals(60, jaeger.getNivelDeConexion());

        // mediumM.mover(cementerio);
        // el demonio no se ve afectado
        assertEquals(60, jaeger.getNivelDeConexion());
    }

    @Test
    public void conexionAngeles_SI_CambiaAlMoverseA_CEMENTERIO() {
        //El medium tiene 1 angel
        rika.aumentarConexionYConectar(mediumM);

        //comprobamos conexion del angel
        assertEquals(70, rika.getNivelDeConexion());

        mediumM.moverA(cementerio);
        // el angel se ve afectado
        assertEquals(65, rika.getNivelDeConexion());
    }

    // Desvinculacion de mediums al mover
    @Test
    void elEspirituSeDesvinculaSiPierdeSuNivelDeConexion() {
        rika.aumentarConexionYConectar(mediumM);
        rika.setNivelDeConexion(1); // seteamos para que se pierda la conexion
        assertEquals(1, rika.getNivelDeConexion());

        mediumM.moverA(cementerio);

        //se desvincularo rika de mediumM
        assertFalse(mediumM.getEspiritus().contains(rika));
        assertTrue(rika.estaLibre());

    }


}
