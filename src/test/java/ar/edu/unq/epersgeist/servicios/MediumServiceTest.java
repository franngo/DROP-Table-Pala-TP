package ar.edu.unq.epersgeist.servicios;


import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Cementerio;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.random.Estrategia.EstrategiaRangoEspecifico;
import ar.edu.unq.epersgeist.modelo.random.RandomDefensa;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MediumServiceTest {
    private static final Integer ENERGIA = 100;

    Espiritu rikaM;
    Espiritu jaegerM;
    Espiritu rikaP;
    Espiritu jaegerP;
    @Autowired
    private EspirituService espirituService;
    @Autowired
    private MediumService serviceMedium;
    @Autowired
    private UbicacionService ubicacionService;
    private Ubicacion berazategui;
    private Ubicacion bernal;
    private Espiritu espirituM;
    private Espiritu espirituM2;
    private Medium epifanioM;
    private Medium ubedaM;
    private Medium epifanioP;
    private Medium ubedaP;


    @BeforeEach
    public void setUp() {
        berazategui = ubicacionService.crear(new Santuario("Berazategui", ENERGIA));
        bernal = ubicacionService.crear(new Santuario("Bernal", ENERGIA));

        espirituM = new Angelical("Charizard", berazategui);
        espirituM2 = new Angelical("Bulbasor", berazategui);
        epifanioM = new Medium("Epifanio el poderoso", 150, 50, bernal);
        ubedaM = new Medium("Ubeda el dt", 100, 20, bernal);

        epifanioP = serviceMedium.crear(epifanioM);
        ubedaP = serviceMedium.crear(ubedaM);

        rikaM = new Angelical("Rika", bernal);
        rikaM.setNivelDeConexion(60);
        rikaP = espirituService.crear(rikaM);

        jaegerM = new Demonio("Jaeger", bernal);
        jaegerM.setNivelDeConexion(50);
        jaegerP = espirituService.crear(jaegerM);
    }

    //crear

    @Test
    void deberiaAsignarIdAlCrearUnMedium() {


        assertNotNull(epifanioP.getId());
        assertNotNull(ubedaP.getId());
    }

    @Test
    void fallaAlCrearMediumNulo() {
        assertThrows(ErrorAlCrearMediumException.class, () -> {
            Medium mediumP = serviceMedium.crear(null);
        });
    }

    @Test
    void noSeCreaUnMediumPorqueYaTieneIdAsignado() {
        Medium matiasM = new Medium("Matias", 150, 50, berazategui);
        matiasM.setId(1L);

        assertThrows(ErrorAlCrearMediumException.class, () -> {
            serviceMedium.crear(matiasM);
        });
    }

    //recuperar
    @Test
    void deberiaRecuperarCorrectamenteLosDatosDelMedium() {
        Medium mediumRecuperado = serviceMedium.recuperar(ubedaP.getId());


        assertEquals(ubedaP.getId(), mediumRecuperado.getId());
        assertEquals(ubedaP.getNombre(), mediumRecuperado.getNombre());
        assertEquals(ubedaP.getMana(), mediumRecuperado.getMana());
        assertEquals(ubedaP.getManaMax(), mediumRecuperado.getManaMax());
        assertEquals(0, serviceMedium.espiritus(ubedaP.getId()).size());
    }

    @Test
    void noSeEncuentraElMediumNoPersistido() {
        Long id = 999L;
        assertThrows(ErrorDeMediumNoEncontradoException.class, () -> {
            serviceMedium.recuperar(id);
        });
    }


    //actualizar

    @Test
    void deberiaActualizarSoloLosCamposModificados1() {


        Medium mediumPrueba = new Medium("MediumTest", 1, 1, berazategui);

        Long id = serviceMedium.crear(mediumPrueba).getId();

        Medium modificado = new Medium("Rolando el audaz", 120, 50, berazategui);
        modificado.setId(id);
        modificado.setMana(15);

        //como modificado tiene el mismo id que epifanio, actualiza dicha fila en la BD
        serviceMedium.actualizar(modificado);

        Medium actualizado = serviceMedium.recuperar(id);

        assertEquals(modificado.getId(), actualizado.getId());
        assertEquals(modificado.getNombre(), actualizado.getNombre());
        assertEquals(modificado.getMana(), actualizado.getMana());
        assertEquals(modificado.getManaMax(), actualizado.getManaMax());
    }

    @Test
    void deberiaActualizarSoloLosCamposModificados2() {

        Medium mediumPrueba = new Medium("MediumTest", 1, 1, berazategui);
        Long id = serviceMedium.crear(mediumPrueba).getId();

        mediumPrueba.setNombre("Rolando el audaz");
        mediumPrueba.setMana(120);

        //se actualiza la fila con el id de epifanio en la DB
        serviceMedium.actualizar(mediumPrueba);

        Medium actualizado = serviceMedium.recuperar(id);

        assertEquals(mediumPrueba.getId(), actualizado.getId());
        assertEquals(mediumPrueba.getNombre(), actualizado.getNombre());
        assertEquals(mediumPrueba.getMana(), actualizado.getMana());
        assertEquals(mediumPrueba.getManaMax(), actualizado.getManaMax());
    }

    @Test
    void noDeberiaActualizarConIdNulo() {
        ErrorAlActualizarMediumSinIdException exception =
                assertThrows(ErrorAlActualizarMediumSinIdException.class, () -> {

                    serviceMedium.actualizar(new Medium("Julian", 22, 22, berazategui));

                });


        assertEquals("No se puede actualizar un medium que no tiene id.", exception.getMessage());
    }

    @Test
    void noDeberiaActualizarConIdQueNoSeEncuentraEnBD() {
        ErrorAlActualizarMediumException exception =
                assertThrows(ErrorAlActualizarMediumException.class, () -> {
                    Medium romanM = new Medium("a", 2, 2, berazategui);
                    romanM.setId(132L); //no hay nada en la DB con ese id, porque arranca vacía
                    serviceMedium.actualizar(romanM);

                });

        assertEquals("No existe en la BD el medium con id: 132", exception.getMessage());
    }

    //eliminar

    @Test
    void deberiaEliminarYNoPoderRecuperarloLuego() {
        Medium mediumPrueba = new Medium("MediumTest", 1, 1, berazategui);
        Long id = serviceMedium.crear(mediumPrueba).getId();

        serviceMedium.eliminar(id);

        assertThrows(ErrorDeMediumNoEncontradoException.class, () -> {
            serviceMedium.recuperar(id);
        });
    }

    //descansar
    @Test
    public void descansarCasoExitoso() {

        Medium mediumPrueba = new Medium("MediumTest", 200, 50, bernal);
        Medium mediumPruebaP = serviceMedium.crear(mediumPrueba);

        rikaP.setNivelDeConexion(0);
        jaegerP.setNivelDeConexion(0);
        espirituService.actualizar(rikaP);
        espirituService.actualizar(jaegerP);

        espirituService.conectar(rikaP.getId(), mediumPruebaP.getId());
        espirituService.conectar(jaegerP.getId(), mediumPruebaP.getId());

        Espiritu rik = espirituService.recuperar(rikaP.getId());
        Espiritu jag = espirituService.recuperar(jaegerP.getId());
        assertEquals(10, rik.getNivelDeConexion());
        assertEquals(10, jag.getNivelDeConexion());

        serviceMedium.descansar(mediumPruebaP.getId());

        Medium med = serviceMedium.recuperar(mediumPruebaP.getId());
        rik = espirituService.recuperar(rikaP.getId());
        jag = espirituService.recuperar(jaegerP.getId());

        assertEquals(200, med.getMana());
        assertEquals(100, rik.getNivelDeConexion());
        assertEquals(10, jag.getNivelDeConexion());
    }


    @Test
    public void descansarFallaPorMediumIdNulo() {
        ErrorDeMediumNoEncontradoException exception = assertThrows(ErrorDeMediumNoEncontradoException.class,
                () -> serviceMedium.descansar(2L));
        assertEquals("No se pudo recuperar al medium con id " + "2" + ".", exception.getMessage());
    }

    //invocar

    @Test
    public void invocarCasoExitoso() {
        //ubedaP es un Medium en bernal, que es un Santuario. Tiene 20 de mana
        assertEquals(20, ubedaP.getMana());

        //espirituM es un Angelical que está en berazategui
        Espiritu espirituP = espirituService.crear(espirituM);

        //al ser un Angelical libre, espirituP puede ser invocado por ubedaP, quien cuenta con suficiente mana para hacerlo
        serviceMedium.invocar(ubedaP.getId(), espirituP.getId());

        Medium mediumActualiado = serviceMedium.recuperar(ubedaP.getId());
        Espiritu espirituActualizado = espirituService.recuperar(espirituP.getId());

        assertEquals(10, mediumActualiado.getMana());
        assertEquals(mediumActualiado.getUbicacion().getNombre(), espirituActualizado.getUbicacion().getNombre());
    }

    @Test
    public void invocarFallaPorMediumIdNulo() {
        ErrorDeMediumNoEncontradoException exception = assertThrows(ErrorDeMediumNoEncontradoException.class,
                () -> serviceMedium.invocar(1L, 2L));
        assertEquals("No se pudo recuperar al medium con id " + "1" + ".", exception.getMessage());
    }


    @Test
    public void invocarFallaPorEspirituIdNulo() {
        Medium mediumPrueba = new Medium("MediumTest", 100, 50, berazategui);
        Medium mediumPruebaP = serviceMedium.crear(mediumPrueba);

        Long idInexistente = mediumPruebaP.getId() + 1;
        ErrorDeEspirituNoEncontradoException exception = assertThrows(ErrorDeEspirituNoEncontradoException.class,
                () -> serviceMedium.invocar(mediumPruebaP.getId(), idInexistente));
        assertEquals("No se pudo recuperar al espíritu con id " + idInexistente + ".", exception.getMessage());
    }

    //exorcizar

    @Test
    void exorcismoNoExitosoDeberiaAfectarElAngelDelMedium() {
        // Por una cuestion de comodidad voy a crear el contexto con nuevas variables
        Ubicacion quilmes = ubicacionService.crear(new Santuario("Quilmes", ENERGIA));

        Medium matiasM = new Medium("Matias", 150, 50, quilmes);

        Medium francoM = new Medium("Franco", 100, 20, quilmes);

        Medium matiasP = serviceMedium.crear(matiasM);
        Medium francoP = serviceMedium.crear(francoM);

        Espiritu omnimanM = new Angelical("Omniman", quilmes);
        omnimanM.setNivelDeConexion(60);
        Espiritu omnimanP = espirituService.crear(omnimanM);

        Espiritu markM = new Demonio("Mark", quilmes);
        markM.setNivelDeConexion(50);
        Espiritu markP = espirituService.crear(markM);
        // el demonio siempre gana
        RandomDefensa.setEstrategiaNumero(new EstrategiaRangoEspecifico(99, 99));


        espirituService.conectar(omnimanP.getId(), matiasP.getId());
        espirituService.conectar(markM.getId(), francoP.getId());

        // ejecuto exorcismo
        serviceMedium.exorcizar(matiasP.getId(), francoP.getId());

        // recupero desde DB
        Espiritu omnimanDerrotado = espirituService.recuperar(omnimanP.getId());

        assertEquals(65.0, omnimanDerrotado.getNivelDeConexion());
    }

    @Test
    void unMediumNoSePuedeExorcizarASiMismo() {
        // Por una cuestion de comodidad voy a crear el contexto con nuevas variables
        Ubicacion quilmes = ubicacionService.crear(new Santuario("Quilmes", ENERGIA));

        Medium matiasM = new Medium("Matias", 150, 50, quilmes);

        Medium matiasP = serviceMedium.crear(matiasM);

        assertThrows(ExorcismoPropioNoPermitidoException.class, () -> serviceMedium.exorcizar(matiasP.getId(), matiasP.getId()));
    }

    @Test
    void unMediumNoPuedeExorcizarAOtroEnUbicacionDiferente() {
        // Por una cuestion de comodidad voy a crear el contexto con nuevas variables
        Ubicacion quilmes = ubicacionService.crear(new Santuario("Quilmes", ENERGIA));
        Ubicacion wilde = ubicacionService.crear(new Santuario("Wilde", ENERGIA));

        Medium matiasM = new Medium("Matias", 150, 50, quilmes);

        Medium francoM = new Medium("Franco", 100, 20, wilde);

        Medium matiasP = serviceMedium.crear(matiasM);
        Medium francoP = serviceMedium.crear(francoM);

        assertThrows(ErrorDeMediumExorcizandoAOtroEnDistintaUbicacion.class, () -> serviceMedium.exorcizar(matiasP.getId(), francoP.getId()));
    }

    //espiritus

    @Test
    public void espiritus() {
        assertEquals(0, serviceMedium.espiritus(ubedaP.getId()).size());

        //ubedaP ya está en la misma ubicación que rikaP y jaegerP
        espirituService.conectar(jaegerP.getId(), ubedaP.getId());
        espirituService.conectar(rikaP.getId(), ubedaP.getId());

        List<Espiritu> es = serviceMedium.espiritus(ubedaP.getId());

        assertEquals(2, es.size());

        assertTrue(es.stream().anyMatch((e) -> e.getId().equals(rikaP.getId())));
        assertTrue(es.stream().anyMatch((e) -> e.getId().equals(jaegerP.getId())));
    }

    // MOVER
    @Test
    void elMediumSeMueveAUnaUbicacionExitosamente() {
        //ubedaP.moverA(bernal);
        serviceMedium.mover(ubedaP.getId(), bernal.getId());

        Medium ubedaAct = serviceMedium.recuperar(ubedaP.getId());

        assertEquals(bernal.getId(), ubedaAct.getUbicacion().getId());

    }

    @Test
    void siSeQuiereMoverALaMismaUbicacionQuedaEnLaMisma() {
        Ubicacion ubiActual = ubedaP.getUbicacion();
        serviceMedium.mover(ubedaP.getId(), ubiActual.getId());

        Medium ubedaAct = serviceMedium.recuperar(ubedaP.getId());

        assertEquals(ubiActual.getId(), ubedaAct.getUbicacion().getId());

    }

    @Test
    void elMediumNoPuedeMoverseAUnaUbicacionInexistente() {
        Ubicacion ubi = new Cementerio("Cementerio", 100);
        ubi.setId(10000l);
        assertThrows(UbicacionNoEncontradaException.class, () -> {

            serviceMedium.mover(ubedaP.getId(), ubi.getId());
        });
    }


    @Test
    void losEspiritusDelMediumSeMuevenConEl() {
        //se conecta Ubeda con 2 espiritus
        espirituService.conectar(jaegerP.getId(), ubedaP.getId());
        espirituService.conectar(rikaP.getId(), ubedaP.getId());

        serviceMedium.mover(ubedaP.getId(), berazategui.getId());

        List<Espiritu> espiritus = serviceMedium.espiritus(ubedaP.getId());

        for (Espiritu e : espiritus) {
            assertEquals(berazategui.getId(), e.getUbicacion().getId());
        }

    }

    @Test
    void losEspiritusNoCambianDeUbicacionSielMediumSeMuevenALaMisma() {
        //se conecta Ubeda con 2 espiritus
        espirituService.conectar(jaegerP.getId(), ubedaP.getId());
        espirituService.conectar(rikaP.getId(), ubedaP.getId());

        Long ubiActual = ubedaP.getUbicacion().getId();
        serviceMedium.mover(ubedaP.getId(), ubiActual);

        List<Espiritu> espiritus = serviceMedium.espiritus(ubedaP.getId());

        for (Espiritu e : espiritus) {
            assertEquals(ubiActual, e.getUbicacion().getId());
        }

    }

    @AfterEach
    void tearDown() {
        espirituService.eliminarTodos();
        serviceMedium.eliminarTodos();
        ubicacionService.eliminarTodos();

    }


}
