package ar.edu.unq.epersgeist.servicios;


import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
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
    private Ubicacion berazategui;

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
        berazategui = ubicacionService.crear(new Ubicacion("Berazategui"));
        bernal = ubicacionService.crear(new Ubicacion("Bernal"));
        espirituM = new Angelical("Charizard", berazategui);
        espirituM2 = new Angelical("Bulbasor", berazategui);

        epifanioM = new Medium("Epifanio el poderoso", 150, 50, berazategui);
        epifanioM.setUbicacion(bernal);

        ubedaM = new Medium("Ubeda el dt", 100, 20, berazategui);
        ubedaM.setUbicacion(bernal);

        epifanioP = serviceMedium.crear(epifanioM);

        ubedaP = serviceMedium.crear(ubedaM);

        rikaM = new Angelical("Rika", berazategui);
        rikaM.setNivelDeConexion(60);
        rikaM.setUbicacion(bernal);
        rikaP = espirituService.crear(rikaM);

        jaegerM = new Demonio("Jaeger", berazategui);
        jaegerM.setNivelDeConexion(50);
        jaegerM.setUbicacion(bernal);
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
        assertEquals(0, ubedaP.getEspiritus().size());
        assertEquals(0, mediumRecuperado.getEspiritus().size());
    }

    @Test
    void noSeEncuentraElMediumNoPersistido() {
        Long id = 999L;
        assertThrows(ErrorDeMediumNoEncontradoException.class, () -> {
            serviceMedium.recuperar(id);
        });
    }

    @Test
    void debeLanzarExcepcionSiElIdEsNull() {
        assertThrows(
                IdMediumInvalidoException.class,
                () -> serviceMedium.recuperar(null)
        );
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
        ErrorAlActualizarMediumException exception =
                assertThrows(ErrorAlActualizarMediumException.class, () -> {

                    serviceMedium.actualizar(new Medium("Julian", 12, 22, berazategui));

                });


        assertEquals("No se puede actualizar un medium que no tiene id.", exception.getMessage());
    }

    @Test
    void noDeberiaActualizarConIdQueNoSeEncuentraEnBD() {
        ErrorAlActualizarMediumException exception =
                assertThrows(ErrorAlActualizarMediumException.class, () -> {
                    Medium romanM = new Medium("a", 1, 2, berazategui);
                    romanM.setId(132L); //no hay nada en la DB con id 1, porque arranca vacía
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

        Medium mediumPrueba = new Medium("MediumTest", 100, 50, berazategui);
        assertEquals(50, mediumPrueba.getMana());
        assertEquals(0, espirituM.getNivelDeConexion());
        assertEquals(0, espirituM2.getNivelDeConexion());

        espirituM.aumentarConexionYConectar(mediumPrueba);
        espirituM2.aumentarConexionYConectar(mediumPrueba);
        Medium mediumPruebaP = serviceMedium.crear(mediumPrueba);

        serviceMedium.descansar(mediumPruebaP.getId());

        Medium m = serviceMedium.recuperar(mediumPruebaP.getId());
        Espiritu e1 = espirituService.recuperar(espirituM.getId());
        Espiritu e2 = espirituService.recuperar(espirituM2.getId());

        assertEquals(65, m.getMana());
        assertEquals(15, e1.getNivelDeConexion());
        assertEquals(15, e2.getNivelDeConexion());
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

        Medium mediumPrueba = new Medium("MediumTest", 100, 50, berazategui);
        Medium mediumP = serviceMedium.crear(mediumPrueba);
        Ubicacion bernalM = new Ubicacion("Quilmes");
        Ubicacion bernalP = ubicacionService.crear(bernalM);
        bernalM.agregarMedium(mediumPrueba);
        ubicacionService.actualizar(bernalM);

        assertEquals(0, bernalM.getEspiritus().size());
        assertEquals(50, mediumPrueba.getMana());
     

        Espiritu espirituP = espirituService.crear(espirituM);

        serviceMedium.invocar(mediumP.getId(), espirituP.getId());

        Medium mediumActualiado = serviceMedium.recuperar(mediumPrueba.getId());
        Espiritu espirituActualizado = espirituService.recuperar(espirituM.getId());

        assertEquals(40, mediumActualiado.getMana());
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
    void exorcismoNoExitosoDeberiaAfectarElAngelDelMedium() throws ExorcismoPropioNoPermitidoException {
        // Por una cuestion de comodidad voy a crear el contexto con nuevas variables
        Ubicacion quilmes = ubicacionService.crear(new Ubicacion("Quilmes"));


        Medium matiasM = new Medium("Matias", 150, 50, berazategui);
        matiasM.setUbicacion(quilmes);

        Medium francoM = new Medium("Franco", 100, 20, berazategui);
        francoM.setUbicacion(quilmes);

        Medium matiasP = serviceMedium.crear(matiasM);
        Medium francoP = serviceMedium.crear(francoM);

        Espiritu omnimanM = new Angelical("Omniman", berazategui);
        omnimanM.setNivelDeConexion(60);
        omnimanM.setUbicacion(quilmes);
        Espiritu omnimanP = espirituService.crear(omnimanM);

        Espiritu markM = new Demonio("Mark", berazategui);
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
