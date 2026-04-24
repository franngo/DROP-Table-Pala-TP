package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlActualizarEspirituException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlConectarEspirituConMedium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDeEspirituNoEncontradoException;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateUbicacionDAO;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateEspirituServiceTest {
    private final EspirituDAO espirituDao = new HibernateEspirituDAO();

    private final MediumDAO mediumDao = new HibernateMediumDAO();
    private final UbicacionDAO ubicacionDao = new HibernateUbicacionDAO();
    private final EspirituService serviceEspiritu = new EspirituServiceImpl(espirituDao, mediumDao);
    private final MediumService mediumService = new MediumServiceImpl(mediumDao, espirituDao);
    private final UbicacionService ubicacionService = new UbicacionServiceImpl(ubicacionDao);
    private Espiritu matias;


    @BeforeEach
    public void setUp() {

        matias = new Espiritu("Matias");
    }

    @Test
    void deberiaAsignarIdAlCrearUnEspiritu() {

        Espiritu espirituP = serviceEspiritu.crear(matias);

        assertNotNull(espirituP.getId());
    }

    @Test
    void deberiaRecuperarCorrectamenteLosDatosDelEspiritu() {

        Espiritu espirituP = serviceEspiritu.crear(matias);
        Espiritu espirituRecuperado = serviceEspiritu.recuperar(espirituP.getId());


        assertEquals(matias.getNombre(), espirituRecuperado.getNombre());
        assertEquals(matias.getNivelDeConexion(), espirituRecuperado.getNivelDeConexion());
    }


    @Test
    void deberiaActualizarSoloLosCamposModificados() {


        Long id = serviceEspiritu.crear(matias).getId();

        // ⚠ nueva instancia (no reutilizar la anterior)
        Espiritu modificado = new Espiritu("Juanchi el pastelero");
        modificado.setId(id);
        modificado.setNivelDeConexion(99);


        serviceEspiritu.actualizar(modificado);

        Espiritu actualizado = serviceEspiritu.recuperar(id);

        assertEquals("Juanchi el pastelero", actualizado.getNombre());
        assertEquals(99, actualizado.getNivelDeConexion());
    }

    @Test
    void noDeberiaActualizarConIdNulo() {

        assertThrows(ErrorAlActualizarEspirituException.class, () -> {
            serviceEspiritu.actualizar(matias);
        });

    }


    @Test
    void deberiaEliminarYNoPoderRecuperarloLuego() {

        Long id = serviceEspiritu.crear(matias).getId();

        serviceEspiritu.eliminar(id);

        assertThrows(ErrorDeEspirituNoEncontradoException.class, () -> {
            serviceEspiritu.recuperar(id);
        });
    }

    @Test
    public void seUsaElServicioParaGuardarModelo() {

        Espiritu espirituP = serviceEspiritu.crear(matias);
        var otroEspiritu = serviceEspiritu.recuperar(espirituP.getId());


        assertEquals(espirituP.getNivelDeConexion(), otroEspiritu.getNivelDeConexion());
        assertEquals(espirituP.getNombre(), otroEspiritu.getNombre());
        assertEquals(espirituP.getId(), otroEspiritu.getId());

        //Pero no son el mismo objeto =(
        //A esto nos referimos con "perdida de identidad"
        Assertions.assertNotEquals(espirituP, otroEspiritu);
    }

    @Test
    void eliminoUnEspirituCorrectamente() {

        Espiritu espirituEliminar = serviceEspiritu.crear(matias);

        assertNotNull(espirituEliminar.getId());

        serviceEspiritu.eliminar(espirituEliminar.getId());

        assertThrows(ErrorDeEspirituNoEncontradoException.class, () -> {
            serviceEspiritu.recuperar(espirituEliminar.getId());
        });
    }

    @Test
    void alNoExistirDemoniosElServicioDevuelveUnaListaVacia() {
        assertTrue(serviceEspiritu.espiritusDemoniacos().isEmpty());
    }

    @Test
    void alExistirAlMenosUnDemonioElServicioDevuelveUnaListaConEllos() {
        Demonio modelDaemon = new Demonio("Dam");
        Demonio anotherModelDaemon = new Demonio("Snap");

        serviceEspiritu.crear(modelDaemon);
        serviceEspiritu.crear(anotherModelDaemon);

        List<Espiritu> listOfDemons = serviceEspiritu.espiritusDemoniacos();
        assertTrue(!listOfDemons.isEmpty());
        assertEquals(listOfDemons.size(), 2);
    }

    @Test
    void losDemoniosSeDevuelvenOrdenadosPorNivelDeConexionDeFormaDescendente() {
        Medium epifanio = new Medium("Epifanio el poderoso", 150, 50);
        Medium ubeda = new Medium("Ubeda el dt", 100, 20);
        Medium chiquiTapia = new Medium("Tapiales", 60, 40);
        Demonio charizard = new Demonio("Charizard");
        Demonio pikuatico = new Demonio("Pikachu Acuatico");
        Demonio bulbasaurio = new Demonio("Bulbasaurio");

        epifanio.agregarEspiritu(charizard);
        chiquiTapia.agregarEspiritu(pikuatico);
        ubeda.agregarEspiritu(bulbasaurio);
        ubeda.descansar();
        ubeda.descansar();
        epifanio.descansar();
        this.executeBlockInPersistentContext(() -> {
            mediumDao.crear(epifanio);
            mediumDao.crear(ubeda);
            mediumDao.crear(chiquiTapia);
            espirituDao.crear(charizard);
            espirituDao.crear(bulbasaurio);
            espirituDao.crear(pikuatico);
        });

        List<Espiritu> daemons = serviceEspiritu.espiritusDemoniacos();

        // No encontré mejor forma usando streams de revisar que cada demonio tenga un nivel mayor
        // o igual de conexión que su consecuente, la clásica es el for pero se vuelve ilegible. -dpolverigiani
        assertEquals(daemons.size(), 3);
        assertTrue(
                IntStream.range(0, daemons.size() - 1)
                        .allMatch(i ->
                                daemons.get(i).getNivelDeConexion() >= daemons.get(i + 1).getNivelDeConexion()
                        )
        );
    }

    @Test
    void losAngelesNoSonRetornadosEnLaListaDeDemonios() {
        Demonio modelDaemon = new Demonio("Dam");
        Angelical modelAngelical = new Angelical("Snap");

        serviceEspiritu.crear(modelDaemon);
        serviceEspiritu.crear(modelAngelical);

        List<Espiritu> listOfDemons = serviceEspiritu.espiritusDemoniacos();
        assertTrue(!listOfDemons.isEmpty());
        assertEquals(listOfDemons.size(), 1);
    }

    @Test
    void recuperarTodosRetornaATodosLosEspiritusDeDistintaNaturaleza() {
        Demonio modelDaemon = new Demonio("Dam");
        Angelical modelAngelical = new Angelical("Snap");

        serviceEspiritu.crear(modelDaemon);
        serviceEspiritu.crear(modelAngelical);

        List<Espiritu> listOfDemons = serviceEspiritu.recuperarTodos();
        assertTrue(!listOfDemons.isEmpty());
        assertEquals(listOfDemons.size(), 2);
    }

    //TODO arreglar
    @Test
    public void seConectaUnEspirituConUnMedium() {
        Ubicacion bernalM = new Ubicacion("Bernal");
        Ubicacion bernalP = ubicacionService.crear(bernalM);
        matias.setUbicacion(bernalP);
        Espiritu espirituP = serviceEspiritu.crear(matias);
        Medium mediumM = new Medium("Juan", 120, 10);

        assertEquals(0, espirituP.getNivelDeConexion());
        mediumM.setUbicacion(bernalP);
        Medium mediumP = mediumService.crear(mediumM);
        Medium medium2 = serviceEspiritu.conectar(espirituP.getId(), mediumM.getId());

        assertFalse(medium2.getEspiritus().isEmpty());

        Espiritu espirituActualizado = serviceEspiritu.recuperar(espirituP.getId());

        assertEquals(2.0, espirituActualizado.getNivelDeConexion());
    }


    @Test
    void noDeberiaConectarUnEspirituInexistenteConUnMedium() {
        Medium medium = new Medium("Juan", 120, 10);

        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> {
            serviceEspiritu.conectar(9999L, medium.getId());
        });
    }

    @Test
    void noDeberiaConectarUnEspirituConUnMediumNulo() {
        Espiritu espiritu = serviceEspiritu.crear(matias);

        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> {
            serviceEspiritu.conectar(espiritu.getId(), null);
        });
    }

    @Test
    void noDeberiaConectarConIdNulo() {
        //Usamo RuntimeExeption ya que es por pasar un objeto null
        Medium medium = new Medium("Juan", 120, 10);
        Medium mediumP = mediumService.crear(medium);
        assertThrows(RuntimeException.class, () -> {
            serviceEspiritu.conectar(null, mediumP.getId());
        });
    }

    @Test
    void deberiaAsociarElEspirituAlMedium() {

        Espiritu espirituP = serviceEspiritu.crear(matias);
        Medium mediumJuan = new Medium("Juan", 120, 10);
        Ubicacion ubicacion = new Ubicacion("Tailandia");
        ubicacion.agregarMedium(mediumJuan);
        ubicacion.agregarEspiritu(espirituP);
        Ubicacion ubicacionP = ubicacionService.crear(ubicacion);

        Medium mediumJuanP = mediumService.crear(mediumJuan);
        Medium mediumConectado = serviceEspiritu.conectar(espirituP.getId(), mediumJuanP.getId());

        Medium mediumJuanActualizadoP = mediumService.recuperar(mediumJuanP.getId());
        assertEquals(1, mediumJuanActualizadoP.getEspiritus().size());
        assertTrue(
                mediumConectado.getEspiritus().stream()
                        .allMatch(e -> e.getId().equals(espirituP.getId()))
        );
    }

    @Test
    void deberiaPersistirElNivelDeConexionLuegoDeConectar() {
        Ubicacion ubicacion = new Ubicacion("Tailandia");
        Medium medium = new Medium("Juan", 120, 25);

        ubicacion.agregarMedium(medium);
        ubicacion.agregarEspiritu(matias);
        Ubicacion ubicacionP = ubicacionService.crear(ubicacion);

        Espiritu espiritu = serviceEspiritu.crear(matias);
        Medium mediumP = mediumService.crear(medium);

        serviceEspiritu.conectar(espiritu.getId(), mediumP.getId());

        Espiritu recuperado = serviceEspiritu.recuperar(espiritu.getId());

        assertEquals(medium.cantidadDeFortalecimientoDeConexion(), recuperado.getNivelDeConexion());
    }
/*

    @Test
    void deberiaPermitirConectarVariosEspiritusAlMismoMedium() {
        Espiritu espiritu1 = serviceEspiritu.crear(new Espiritu("Matias"));
        Espiritu espiritu2 = serviceEspiritu.crear(new Espiritu("Juanchi el Pastelero"));

        Medium medium = new Medium("Juan", 120, 10);

        serviceEspiritu.conectar(espiritu1.getId(), medium.getId());
        Medium mediumActualizado = serviceEspiritu.conectar(espiritu2.getId(), medium.getId());

        assertEquals(2, mediumActualizado.getEspiritus().size());
    }


    @Test
    void noDeberiaConectarUnEspirituEliminado() {
        Espiritu espiritu = serviceEspiritu.crear(matias);
        serviceEspiritu.eliminar(espiritu.getId());

        Medium medium = new Medium("Juan", 120, 10);

        assertThrows(RuntimeException.class, () -> {
            serviceEspiritu.conectar(espiritu.getId(), medium.getId());
        });
    }

    @Test
    void deberiaMantenerLosDatosOriginalesDelEspirituLuegoDeConectar() {
        Espiritu espiritu = serviceEspiritu.crear(matias);
        String nombreOriginal = espiritu.getNombre();
        Medium medium = new Medium("Juan", 120, 10);

        serviceEspiritu.conectar(espiritu.getId(), medium.getId());
        Espiritu recuperado = serviceEspiritu.recuperar(espiritu.getId());

        assertEquals(nombreOriginal, recuperado.getNombre());
    }
*/


    private void executeBlockInPersistentContext(Runnable block) {
        HibernateTransactionRunner.runTrx(() -> {
            block.run();
            return null;
        });
    }

    @AfterEach
    void tearDown() {
        this.executeBlockInPersistentContext(espirituDao::eliminarTodos);
    }


}
