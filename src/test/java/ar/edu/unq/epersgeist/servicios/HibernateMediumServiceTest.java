package ar.edu.unq.epersgeist.servicios;


import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlActualizarMediumException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDeEspirituNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDeMediumNoEncontradoException;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.modelo.exceptions.ExorcismoPropioNoPermitidoException;
import ar.edu.unq.epersgeist.modelo.random.Estrategia.EstrategiaRangoEspecifico;
import ar.edu.unq.epersgeist.modelo.random.RandomDefensa;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateUbicacionDAO;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateMediumServiceTest {

    private final HibernateMediumDAO mediumDao = new HibernateMediumDAO();
    private final HibernateEspirituDAO espirituDao = new HibernateEspirituDAO();
    private final EspirituService espirituService = new EspirituServiceImpl(espirituDao, mediumDao);
    private final MediumService serviceMedium = new MediumServiceImpl(mediumDao, espirituDao);
    UbicacionService ubicacionService = new UbicacionServiceImpl(new HibernateUbicacionDAO());


    private Espiritu espirituM;
    private Espiritu espirituM2;
    private Medium epifanioM;
    private Medium ubedaM;
    private Medium epifanioP;
    private Medium ubedaP;

    Espiritu rikaM;
    Espiritu jaegerM;

    Espiritu rikaP;
    Espiritu jaegerP;
    Ubicacion bernal;

    @BeforeEach
    public void setUp() {
        //bernal = ubicacionService.crear(new Ubicacion("Bernal"));
        espirituM = new Espiritu("Charizard");
        espirituM2 = new Espiritu("Bulbasor");

        epifanioM = new Medium("Epifanio el poderoso", 150, 50);
        // epifanioM.setUbicacion(bernal);

        ubedaM = new Medium("Ubeda el dt", 100, 20);
        //  ubedaM.setUbicacion(bernal);

        epifanioP = serviceMedium.crear(epifanioM);
        ubedaP = serviceMedium.crear(ubedaM);

        rikaM = new Angelical("Rika");
        rikaM.setNivelDeConexion(60);
        //rikaM.setUbicacion(bernal);
        rikaP = espirituService.crear(rikaM);

        jaegerM = new Demonio("Jaeger");
        jaegerM.setNivelDeConexion(50);
        //jaegerM.setUbicacion(bernal);
        jaegerP = espirituService.crear(jaegerM);


    }

    @Test
    void deberiaAsignarIdAlCrearUnMedium() {


        assertNotNull(epifanioP.getId());
        assertNotNull(ubedaP.getId());
    }


    @Test
    void deberiaRecuperarCorrectamenteLosDatosDelMedium() {
        Medium mediumRecuperado = serviceMedium.recuperar(ubedaP.getId());


        assertEquals(ubedaP.getId(), mediumRecuperado.getId());
        assertEquals(ubedaP.getNombre(), mediumRecuperado.getNombre());
        assertEquals(ubedaP.getMana(), mediumRecuperado.getMana());
        assertEquals(ubedaP.getManaMax(), mediumRecuperado.getManaMax());
        assertEquals(0, ubedaP.getEspiritus().size());
        assertEquals(0, mediumRecuperado.getEspiritus().size());
    }


    @Test
    void deberiaActualizarSoloLosCamposModificados1() {

        Long id = serviceMedium.crear(epifanioM).getId();

        Medium modificado = new Medium("Rolando el audaz", 120, 50);
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

        Long id = serviceMedium.crear(epifanioM).getId();

        epifanioM.setNombre("Rolando el audaz");
        epifanioM.setMana(120);

        //se actualiza la fila con el id de epifanio en la DB
        serviceMedium.actualizar(epifanioM);

        Medium actualizado = serviceMedium.recuperar(id);

        assertEquals(epifanioM.getId(), actualizado.getId());
        assertEquals(epifanioM.getNombre(), actualizado.getNombre());
        assertEquals(epifanioM.getMana(), actualizado.getMana());
        assertEquals(epifanioM.getManaMax(), actualizado.getManaMax());
    }

    @Test
    void noDeberiaActualizarConIdNulo() {
        ErrorAlActualizarMediumException exception =
                assertThrows(ErrorAlActualizarMediumException.class, () -> {

                    serviceMedium.actualizar(new Medium("Julian", 12, 22));

                });


        assertEquals("No se puede actualizar un medium que no tiene id.", exception.getMessage());
    }

    @Test
    void noDeberiaActualizarConIdQueNoSeEncuentraEnBD() {
        ErrorAlActualizarMediumException exception =
                assertThrows(ErrorAlActualizarMediumException.class, () -> {
                    Medium romanM = new Medium("a", 1, 2);
                    romanM.setId(132L); //no hay nada en la DB con id 1, porque arranca vacía
                    serviceMedium.actualizar(romanM);

                });

        assertEquals("No existe en la BD el medium con id: 132", exception.getMessage());
    }

    @Test
    void noSeEncuentraElMediumNoPersistido() {
        Long id = 999L;
        assertThrows(ErrorDeMediumNoEncontradoException.class, () -> {
            serviceMedium.recuperar(id);
        });
    }

    @Test
    void deberiaEliminarYNoPoderRecuperarloLuego() {

        Long id = serviceMedium.crear(ubedaM).getId();

        serviceMedium.eliminar(id);

        assertThrows(ErrorDeMediumNoEncontradoException.class, () -> {
            serviceMedium.recuperar(id);
        });
    }


    @Test
    public void descansarCasoExitoso() {
        assertEquals(50, epifanioM.getMana());
        assertEquals(0, espirituM.getNivelDeConexion());
        assertEquals(0, espirituM2.getNivelDeConexion());

        epifanioM.agregarEspiritu(espirituM);
        epifanioM.agregarEspiritu(espirituM2);
        Medium epifanioP = serviceMedium.crear(epifanioM);

        serviceMedium.descansar(epifanioP.getId());

        Medium m = serviceMedium.recuperar(epifanioM.getId());
        Espiritu e1 = espirituService.recuperar(espirituM.getId());
        Espiritu e2 = espirituService.recuperar(espirituM2.getId());

        assertEquals(65, m.getMana());
        assertEquals(5, e1.getNivelDeConexion());
        assertEquals(5, e2.getNivelDeConexion());
    }


    @Test
    public void descansarFallaPorMediumIdNulo() {
        ErrorDeMediumNoEncontradoException exception = assertThrows(ErrorDeMediumNoEncontradoException.class,
                () -> serviceMedium.descansar(2L));
        assertEquals("No se pudo recuperar al medium con id " + "2" + ".", exception.getMessage());
    }

    @Test
    public void invocarCasoExitoso() {


        Ubicacion bernalM = new Ubicacion("Quilmes");
        epifanioM.setUbicacion(bernalM);

        assertEquals(50, epifanioM.getMana());
        assertNull(espirituM.getUbicacion());
        assertEquals(0, bernalM.getEspiritus().size());

        Ubicacion bernalP = ubicacionService.crear(bernalM);
        Medium mediumP = serviceMedium.crear(epifanioM);
        Espiritu espirituP = espirituService.crear(espirituM);

        serviceMedium.invocar(mediumP.getId(), espirituP.getId());

        Ubicacion bernalActualizado = ubicacionService.recuperar(bernalP.getId());
        Medium mediumActualiado = serviceMedium.recuperar(epifanioM.getId());
        Espiritu espirituActualizado = espirituService.recuperar(espirituM.getId());


        assertEquals(40, mediumActualiado.getMana());
        assertEquals(mediumActualiado.getUbicacion().getNombre(), espirituActualizado.getUbicacion().getNombre());

        assertEquals(1, bernalActualizado.getEspiritus().size());

    }

    @Test
    public void invocarFallaPorMediumIdNulo() {
        ErrorDeMediumNoEncontradoException exception = assertThrows(ErrorDeMediumNoEncontradoException.class,
                () -> serviceMedium.invocar(1L, 2L));
        assertEquals("No se pudo recuperar al medium con id " + "1" + ".", exception.getMessage());
    }


    @Test
    public void invocarFallaPorEspirituIdNulo() {

        Medium epifanioP = serviceMedium.crear(epifanioM);

        Long idInexistente = epifanioP.getId() + 1;
        ErrorDeEspirituNoEncontradoException exception = assertThrows(ErrorDeEspirituNoEncontradoException.class,
                () -> serviceMedium.invocar(epifanioM.getId(), idInexistente));
        assertEquals("No se pudo recuperar al espíritu con id " + idInexistente + ".", exception.getMessage());
    }

//    private void executeBlockInPersistentContext(Runnable block) {
//        HibernateTransactionRunner.runTrx(() -> {
//            block.run();
//            return null;
//        });
//    }


    @Test
    void exorcismoNoExitosoDeberiaAfectarElAngelDelMedium() throws ExorcismoPropioNoPermitidoException {
        // Por una cuestion de comodidad voy a crear el contexto con nuevas variables
        Ubicacion quilmes = ubicacionService.crear(new Ubicacion("Quilmes"));


        Medium matiasM = new Medium("Matias", 150, 50);
        matiasM.setUbicacion(quilmes);

        Medium francoM = new Medium("Franco", 100, 20);
        francoM.setUbicacion(quilmes);

        Medium matiasP = serviceMedium.crear(matiasM);
        Medium francoP = serviceMedium.crear(francoM);

        Espiritu omnimanM = new Angelical("Omniman");
        omnimanM.setNivelDeConexion(60);
        omnimanM.setUbicacion(quilmes);
        Espiritu omnimanP = espirituService.crear(omnimanM);

        Espiritu markM = new Demonio("Mark");
        markM.setNivelDeConexion(50);
        markM.setUbicacion(quilmes);
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

    @AfterEach
    void tearDown() {
        espirituService.eliminarTodos();
        serviceMedium.eliminarTodos();
        ubicacionService.eliminarTodos();

    }


}
