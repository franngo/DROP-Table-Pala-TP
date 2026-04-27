package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlActualizarEspirituException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlConectarEspirituConMedium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlCrearEspirituException;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

        matias = new Angelical("Matias");
    }

    @Test
    void seCreaUnEspirituCorrectamente() {
        Espiritu espirituP = serviceEspiritu.crear(matias);
        assertNotNull(espirituP.getId());
    }

    @Test
    void fallaAlCrearEspirituNulo() {
        Espiritu espiritu = null;
        assertThrows(ErrorAlCrearEspirituException.class, () -> {
            Espiritu espirituP = serviceEspiritu.crear(espiritu);
        });
    }

    @Test
    void deberiaAsignarIdAlCrearUnEspiritu() {

        Espiritu espirituP = serviceEspiritu.crear(matias);

        assertNotNull(espirituP.getId());
    }

    @Test
    void noSeCreaUnEspirituPorqueYaTieneIdAsignado() {
        matias.setId(1L);
        assertThrows(ErrorAlCrearEspirituException.class, () -> {
            serviceEspiritu.crear(matias);
        });


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
        Espiritu modificado = new Angelical("Juanchi el pastelero");
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
        assertFalse(listOfDemons.isEmpty());
        assertEquals(2, listOfDemons.size());
    }

    @Test
    void losDemoniosSeDevuelvenOrdenadosPorNivelDeConexionDeFormaDescendente() {
//
        Demonio charizard = new Demonio("Charizard");
        Demonio pikuatico = new Demonio("Pikachu Acuatico");
        Demonio bulbasaurio = new Demonio("Bulbasaurio");

        charizard.setNivelDeConexion(30);
        pikuatico.setNivelDeConexion(20);
        bulbasaurio.setNivelDeConexion(50);

        Espiritu charizardP = serviceEspiritu.crear(charizard);
        Espiritu bulbasaurioP = serviceEspiritu.crear(bulbasaurio);
        Espiritu pikuaticoP = serviceEspiritu.crear(pikuatico);

        List<Espiritu> daemons = serviceEspiritu.espiritusDemoniacos();

        // compruebo que el orden sea correcto
        assertEquals(3, daemons.size());
        assertEquals(daemons.get(0).getId(), bulbasaurioP.getId());
        assertEquals(daemons.get(1).getId(), charizardP.getId());
        assertEquals(daemons.get(2).getId(), pikuaticoP.getId());

    }

    @Test
    void losAngelesNoSonRetornadosEnLaListaDeDemonios() {
        Demonio modelDaemon = new Demonio("Dam");
        Angelical modelAngelical = new Angelical("Snap");

        serviceEspiritu.crear(modelDaemon);
        serviceEspiritu.crear(modelAngelical);

        List<Espiritu> listOfDemons = serviceEspiritu.espiritusDemoniacos();
        assertFalse(listOfDemons.isEmpty());
        assertEquals(1, listOfDemons.size());
    }

    @Test
    void recuperarTodosRetornaATodosLosEspiritusDeDistintaNaturaleza() {
        Demonio modelDaemon = new Demonio("Dam");
        Angelical modelAngelical = new Angelical("Snap");

        serviceEspiritu.crear(modelDaemon);
        serviceEspiritu.crear(modelAngelical);

        List<Espiritu> listOfDemons = serviceEspiritu.recuperarTodos();
        assertFalse(listOfDemons.isEmpty());
        assertEquals(2, listOfDemons.size());
    }

    @Test
    void losDemoniosSeDevuelvenOrdenadosPorNivelDeConexionDeFormaAscendenteYPaginados() {
        Espiritu charizard = new Demonio("Charizard");
        charizard.setNivelDeConexion(95);
        serviceEspiritu.crear(charizard);

        Espiritu bulbasaur = new Demonio("bulbasaur");
        bulbasaur.setNivelDeConexion(90);
        serviceEspiritu.crear(bulbasaur);

        Espiritu pikachu = new Demonio("pikachu");
        pikachu.setNivelDeConexion(80);
        serviceEspiritu.crear(pikachu);

        Espiritu ghastly = new Demonio("ghastly");
        ghastly.setNivelDeConexion(65);
        serviceEspiritu.crear(ghastly);

        Espiritu pichu = new Demonio("pichu");
        pichu.setNivelDeConexion(55);
        serviceEspiritu.crear(pichu);

        Espiritu raichu = new Demonio("raichu");
        raichu.setNivelDeConexion(45);
        serviceEspiritu.crear(raichu);

        Espiritu magikarp = new Demonio("magikarp");
        magikarp.setNivelDeConexion(5);
        serviceEspiritu.crear(magikarp);


        List<Espiritu> daemons = serviceEspiritu.espiritusDemoniacos(Direccion.ASCENDENTE, 1, 3);

        assertEquals(3, daemons.size());
        assertEquals(daemons.get(0).getId(), magikarp.getId());
        assertEquals(daemons.get(1).getId(), raichu.getId());
        assertEquals(daemons.get(2).getId(), pichu.getId());

        List<Espiritu> daemons2 = serviceEspiritu.espiritusDemoniacos(Direccion.ASCENDENTE, 2, 3);

        assertEquals(3, daemons.size());
        assertEquals(daemons2.get(0).getId(), ghastly.getId());
        assertEquals(daemons2.get(1).getId(), pikachu.getId());
        assertEquals(daemons2.get(2).getId(), bulbasaur.getId());
    }


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
        Espiritu nuevoEspiritu = new Angelical("Matias");
        Espiritu nuevoEspirituP = serviceEspiritu.crear(nuevoEspiritu);

        Medium mediumJuan = new Medium("Juan", 120, 10);
        Ubicacion ubicacion = new Ubicacion("Tailandia");
        Ubicacion ubicacionP = ubicacionService.crear(ubicacion);

        ubicacion.agregarMedium(mediumJuan);
        ubicacion.agregarEspiritu(nuevoEspirituP);

        Medium mediumJuanP = mediumService.crear(mediumJuan);
        ubicacionService.actualizar(ubicacionP);

        Medium mediumConectado = serviceEspiritu.conectar(nuevoEspirituP.getId(), mediumJuanP.getId());

        Medium mediumJuanActualizadoP = mediumService.recuperar(mediumJuanP.getId());
        assertEquals(1, mediumJuanActualizadoP.getEspiritus().size());
        assertTrue(
                mediumConectado.getEspiritus().stream()
                        .allMatch(e -> e.getId().equals(nuevoEspirituP.getId()))
        );
    }

    @Test
    void deberiaPersistirElNivelDeConexionLuegoDeConectar() {
        Ubicacion ubicacion = new Ubicacion("Tailandia");
        Ubicacion ubicacionP = ubicacionService.crear(ubicacion);
        Medium medium = new Medium("Juan", 120, 25);
        Espiritu nuevoEspiritu = new Angelical("Juan");


        ubicacion.agregarMedium(medium);
        ubicacion.agregarEspiritu(nuevoEspiritu);
        Medium mediumP = mediumService.crear(medium);
        Espiritu espirituP = serviceEspiritu.crear(nuevoEspiritu);
        ubicacionService.actualizar(ubicacionP);


        serviceEspiritu.conectar(espirituP.getId(), mediumP.getId());

        Espiritu recuperado = serviceEspiritu.recuperar(espirituP.getId());

        assertEquals(mediumP.cantidadDeFortalecimientoDeConexion(), recuperado.getNivelDeConexion());
    }

    @Test
    void deberiaPermitirConectarVariosEspiritusAlMismoMedium() {
        Ubicacion ubicacion = new Ubicacion("Tailandia");
        Ubicacion ubiPersistida = ubicacionService.crear(ubicacion);
        Medium medium = new Medium("Juan", 120, 10);
        Espiritu pepe = new Angelical("Pepe");
        Espiritu raul = new Angelical("Raul");
        ubicacion.agregarEspiritu(pepe);
        ubicacion.agregarEspiritu(raul);
        ubicacion.agregarMedium(medium);

        Espiritu espiritu1 = serviceEspiritu.crear(pepe);
        Espiritu espiritu2 = serviceEspiritu.crear(raul);

        Medium mediumP = mediumService.crear(medium);
        ubicacionService.actualizar(ubiPersistida);
        serviceEspiritu.conectar(espiritu1.getId(), mediumP.getId());
        serviceEspiritu.conectar(espiritu2.getId(), mediumP.getId());

        // Medium mediumActualizado = serviceEspiritu.conectar(espiritu2.getId(), mediumP.getId());
        Medium mediumActualizado = mediumService.recuperar(mediumP.getId());
        assertEquals(2, mediumActualizado.getEspiritus().size());
    }


    @Test
    void noDeberiaConectarUnEspirituEliminado() {
        Espiritu espiritu = serviceEspiritu.crear(matias);
        serviceEspiritu.eliminar(espiritu.getId());

        Medium medium = new Medium("Juan", 120, 10);

        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> {
            serviceEspiritu.conectar(espiritu.getId(), medium.getId());
        });
    }


    @Test
    void deberiaMantenerLosDatosOriginalesDelEspirituLuegoDeConectar() {
        Medium medium = new Medium("Juan", 120, 10);
        mediumService.crear(medium);
        serviceEspiritu.crear(matias);
        // agregamos la ubicacion de los actores
        Ubicacion ubicacion = new Ubicacion("Tailandia");
        ubicacion.agregarEspiritu(matias);
        ubicacion.agregarMedium(medium);
        ubicacionService.crear(ubicacion);

        String nombreOriginal = matias.getNombre();

        serviceEspiritu.conectar(matias.getId(), medium.getId());
        Espiritu recuperado = serviceEspiritu.recuperar(matias.getId());

        assertEquals(nombreOriginal, recuperado.getNombre());
    }

    @AfterEach
    void tearDown() {
        serviceEspiritu.eliminarTodos();
        mediumService.eliminarTodos();
        ubicacionService.eliminarTodos();

    }


}
