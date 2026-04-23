package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlActualizarMediumException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlRecuperarMediumException;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateMediumServiceTest {


    private final MediumService serviceMedium = new MediumServiceImpl(new HibernateMediumDAO());
    private final EspirituService espirituService = new EspirituServiceImpl(new HibernateEspirituDAO());
    private Medium epifanioM;
    private Medium ubedaM;
    private Medium epifanioP;
    private Medium ubedaP;

    Espiritu rikaM;
    Espiritu jaegerM;

    Espiritu rikaP;
    Espiritu jaegerP;

    @BeforeEach
    public void setUp() {
        epifanioM = new Medium("Epifanio el poderoso", 150, 50);
        ubedaM = new Medium("Ubeda el dt", 100, 20);

        epifanioP = serviceMedium.crear(epifanioM);
        ubedaP = serviceMedium.crear(ubedaM);

        rikaM = new Angelical("Rika");
        rikaM.setNivelDeConexion(60);
        rikaP = espirituService.crear(rikaM);

        jaegerM = new Demonio("Jaeger");
        jaegerM.setNivelDeConexion(50);
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
        assertEquals(ubedaP.getCantFortalece(), mediumRecuperado.getCantFortalece());
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
        assertEquals(modificado.getCantFortalece(), actualizado.getCantFortalece());
    }

    @Test
    void deberiaActualizarSoloLosCamposModificados2() {

        Medium mediumRecuperado = serviceMedium.recuperar(epifanioP.getId());

        mediumRecuperado.setNombre("Rolando el audaz");
        mediumRecuperado.setMana(120);


        serviceMedium.actualizar(mediumRecuperado);

        Medium actualizado = serviceMedium.recuperar(mediumRecuperado.getId());

        assertEquals(mediumRecuperado.getId(), actualizado.getId());
        assertEquals(mediumRecuperado.getNombre(), actualizado.getNombre());
        assertEquals(mediumRecuperado.getMana(), actualizado.getMana());
        assertEquals(mediumRecuperado.getManaMax(), actualizado.getManaMax());
        assertEquals(mediumRecuperado.getCantFortalece(), actualizado.getCantFortalece());
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
        assertThrows(ErrorAlRecuperarMediumException.class, () -> {
            serviceMedium.recuperar(id);
        });
    }

    @Test
    void deberiaEliminarYNoPoderRecuperarloLuego() {

        Long id = serviceMedium.crear(ubedaM).getId();

        serviceMedium.eliminar(id);

        assertThrows(ErrorAlRecuperarMediumException.class, () -> {
            serviceMedium.recuperar(id);
        });
    }
/*
    // TODO completar cuando haya conectar
    @Test
    void exorcismoNoExitosoDeberiaAfectarElAngelDelMedium() throws ExorcismoPropioNoPermitidoException {

        // el demonio siempre gana
        RandomDefensa.setEstrategiaNumero(new EstrategiaRangoEspecifico(99, 99));

        // creo y persisto mediums CON sus espíritus en la misma transacción
        Long idExorcizador = serviceMedium.recuperar(ubedaP.getId()).getId();


        // serviceMedium.conectar(rikaP.getId(),ubedaP.getId());


        Long idAtacado = serviceMedium.recuperar(epifanioP.getId()).getId();
        // serviceMedium.conectar(jaegerP.getId(),epifanioP.getId());

        // ejecuto exorcismo

        serviceMedium.exorcizar(idExorcizador, idAtacado);


        // recupero desde DB
        Medium mediumDerrotado = serviceMedium.recuperar(idExorcizador);
        Espiritu rikaDerrotada = espirituService.recuperar(rikaP.getId());


        assertEquals(55, rikaDerrotada.getNivelDeConexion());
    }
*/

    @AfterEach
    void tearDown() {

        espirituService.eliminarTodos();
        serviceMedium.eliminarTodos();


    }
}
