package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlConectarEspirituConMedium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlCrearEspirituException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDeEspirituNoEncontradoException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EspirituServiceTest {

    private Espiritu matias;
    private Ubicacion berazategui;

    @Autowired
    private EspirituService serviceEspiritu;


    @Autowired
    private MediumService mediumService;
    @Autowired
    private UbicacionService ubicacionService;

    @BeforeEach
    public void setUp() {
        berazategui = ubicacionService.crear(new Ubicacion("Berazategui"));
        matias = new Angelical("Matias", berazategui);
    }

    //crear

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
    void noSeCreaUnEspirituPorqueYaTieneIdAsignado() {
        matias.setId(1L);

        assertThrows(ErrorAlCrearEspirituException.class, () -> {
            serviceEspiritu.crear(matias);
        });
    }

    //recuperar

    @Test
    void deberiaRecuperarCorrectamenteLosDatosDelEspiritu() {

        Espiritu espirituP = serviceEspiritu.crear(matias);
        Espiritu espirituRecuperado = serviceEspiritu.recuperar(espirituP.getId());


        assertEquals(matias.getNombre(), espirituRecuperado.getNombre());
        assertEquals(matias.getNivelDeConexion(), espirituRecuperado.getNivelDeConexion());
    }


    @Test
    void noSeEncuentraElEspirituNoPersistido() {
        Long id = 999L;
        assertThrows(ErrorDeEspirituNoEncontradoException.class, () -> {
            serviceEspiritu.recuperar(id);
        });
    }

    //actualizar

    @Test
    void deberiaActualizarSoloLosCamposModificados() {


        Long id = serviceEspiritu.crear(matias).getId();

        // ⚠ nueva instancia (no reutilizar la anterior)
        Espiritu modificado = new Angelical("Juanchi el pastelero", berazategui);
        modificado.setId(id);
        modificado.setNivelDeConexion(99);


        serviceEspiritu.actualizar(modificado);

        Espiritu actualizado = serviceEspiritu.recuperar(id);

        assertEquals("Juanchi el pastelero", actualizado.getNombre());
        assertEquals(99, actualizado.getNivelDeConexion());
    }


    //eliminar

    @Test
    void deberiaEliminarYNoPoderRecuperarloLuego() {

        Long id = serviceEspiritu.crear(matias).getId();

        serviceEspiritu.eliminar(id);

        assertThrows(ErrorDeEspirituNoEncontradoException.class, () -> {
            serviceEspiritu.recuperar(id);
        });
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

    //

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

    //espiritusDemoniacos

    @Test
    void alNoExistirDemoniosElServicioDevuelveUnaListaVacia() {
        assertTrue(serviceEspiritu.espiritusDemoniacos().isEmpty());
    }

    @Test
    void alExistirAlMenosUnDemonioElServicioDevuelveUnaListaConEllos() {
        Demonio modelDaemon = new Demonio("Dam", berazategui);
        Demonio anotherModelDaemon = new Demonio("Snap", berazategui);

        serviceEspiritu.crear(modelDaemon);
        serviceEspiritu.crear(anotherModelDaemon);

        List<Demonio> listOfDemons = serviceEspiritu.espiritusDemoniacos();
        assertFalse(listOfDemons.isEmpty());
        assertEquals(2, listOfDemons.size());
    }

    @Test
    void losDemoniosSeDevuelvenOrdenadosPorNivelDeConexionDeFormaDescendente() {

        Demonio charizard = new Demonio("Charizard", berazategui);
        Demonio pikuatico = new Demonio("Pikachu Acuatico", berazategui);
        Demonio bulbasaurio = new Demonio("Bulbasaurio", berazategui);

        charizard.setNivelDeConexion(30);
        pikuatico.setNivelDeConexion(20);
        bulbasaurio.setNivelDeConexion(50);

        Espiritu charizardP = serviceEspiritu.crear(charizard);
        Espiritu bulbasaurioP = serviceEspiritu.crear(bulbasaurio);
        Espiritu pikuaticoP = serviceEspiritu.crear(pikuatico);

        List<Demonio> daemons = serviceEspiritu.espiritusDemoniacos();

        // compruebo que el orden sea correcto
        assertEquals(3, daemons.size());
        assertEquals(daemons.get(0).getId(), bulbasaurioP.getId());
        assertEquals(daemons.get(1).getId(), charizardP.getId());
        assertEquals(daemons.get(2).getId(), pikuaticoP.getId());

    }

    @Test
    void losAngelesNoSonRetornadosEnLaListaDeDemonios() {
        Demonio modelDaemon = new Demonio("Dam", berazategui);
        Angelical modelAngelical = new Angelical("Snap", berazategui);

        serviceEspiritu.crear(modelDaemon);
        serviceEspiritu.crear(modelAngelical);

        List<Demonio> listOfDemons = serviceEspiritu.espiritusDemoniacos();
        assertFalse(listOfDemons.isEmpty());
        assertEquals(1, listOfDemons.size());
    }

    //espiritusDemoniacos (con paginación)

    @Test
    void losDemoniosSeDevuelvenOrdenadosPorNivelDeConexionDeFormaAscendenteYPaginados() {
        Espiritu charizard = new Demonio("Charizard", berazategui);
        charizard.setNivelDeConexion(95);
        serviceEspiritu.crear(charizard);

        Espiritu bulbasaur = new Demonio("bulbasaur", berazategui);
        bulbasaur.setNivelDeConexion(90);
        serviceEspiritu.crear(bulbasaur);

        Espiritu pikachu = new Demonio("pikachu", berazategui);
        pikachu.setNivelDeConexion(80);
        serviceEspiritu.crear(pikachu);

        Espiritu ghastly = new Demonio("ghastly", berazategui);
        ghastly.setNivelDeConexion(65);
        serviceEspiritu.crear(ghastly);

        Espiritu pichu = new Demonio("pichu", berazategui);
        pichu.setNivelDeConexion(55);
        serviceEspiritu.crear(pichu);

        Espiritu raichu = new Demonio("raichu", berazategui);
        raichu.setNivelDeConexion(45);
        serviceEspiritu.crear(raichu);

        Espiritu magikarp = new Demonio("magikarp", berazategui);
        magikarp.setNivelDeConexion(5);
        serviceEspiritu.crear(magikarp);


        Page<Demonio> daemons = serviceEspiritu.espiritusDemoniacos(Direccion.ASCENDENTE, 1, 3);
        List<Demonio> demoniosList = daemons.getContent();
        assertEquals(3, demoniosList.size());
        assertEquals(demoniosList.get(0).getId(), magikarp.getId());
        assertEquals(demoniosList.get(1).getId(), raichu.getId());
        assertEquals(demoniosList.get(2).getId(), pichu.getId());

        Page<Demonio> daemons2 = serviceEspiritu.espiritusDemoniacos(Direccion.ASCENDENTE, 2, 3);
        List<Demonio> demoniosList2 = daemons2.getContent();
        assertEquals(3, demoniosList2.size());

        assertEquals(demoniosList2.get(0).getId(), ghastly.getId());


        assertEquals(demoniosList2.get(0).getId(), ghastly.getId());
        assertEquals(demoniosList2.get(1).getId(), pikachu.getId());
        assertEquals(demoniosList2.get(2).getId(), bulbasaur.getId());
    }

    //recuperarTodos

    @Test
    void recuperarTodosRetornaATodosLosEspiritusDeDistintaNaturaleza() {
        Demonio modelDaemon = new Demonio("Dam", berazategui);
        Angelical modelAngelical = new Angelical("Snap", berazategui);

        serviceEspiritu.crear(modelDaemon);
        serviceEspiritu.crear(modelAngelical);

        List<Espiritu> listOfDemons = serviceEspiritu.recuperarTodos();
        assertFalse(listOfDemons.isEmpty());
        assertEquals(2, listOfDemons.size());
    }

    //conectar


    @Test
    void noDeberiaConectarUnEspirituInexistenteConUnMedium() {
        Medium medium = new Medium("Juan", 120, 10, berazategui);
        Medium mediumP = mediumService.crear(medium);

        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> {
            serviceEspiritu.conectar(9999L, mediumP.getId());
        });
    }

    @Test
    void noDeberiaConectarUnEspirituConUnMediumInexistente() {
        Espiritu espiritu = serviceEspiritu.crear(matias);

        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> {
            serviceEspiritu.conectar(espiritu.getId(), 9999L);
        });
    }

    @Test
    void noDeberiaConectarUnEspirituEliminado() {
        Espiritu espiritu = serviceEspiritu.crear(matias);
        serviceEspiritu.eliminar(espiritu.getId()); //eliminado de la DB, pero el objeto en la VM sigue con su id

        Medium medium = new Medium("Juan", 120, 10, berazategui);
        Medium mediumP = mediumService.crear(medium);

        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> {
            serviceEspiritu.conectar(espiritu.getId(), mediumP.getId());
        });
    }

    @Test
    void noDeberiaConectarUnEspirituQueEstaEnDiferenteUbicacion() {
        Ubicacion bernalM = new Ubicacion("Bernal");
        Ubicacion bernalP = ubicacionService.crear(bernalM);

        Medium juan = new Medium("Juan", 120, 20, berazategui);

        bernalP.agregarMedium(juan);
        Medium juanP = mediumService.crear(juan);

        Espiritu espiritu = serviceEspiritu.crear(matias);
        Espiritu espiritu2 = mediumService.invocar(juanP.getId(), espiritu.getId());

        //ahora espiritu2 está en una Ubicacion
        assertEquals(espiritu2.getUbicacion().getId(), juanP.getUbicacion().getId());

        Ubicacion EzpeletaM = new Ubicacion("Ezpeleta");
        Ubicacion EzpeletaP = ubicacionService.crear(EzpeletaM);

        Medium lolo = new Medium("Lolo", 120, 20, berazategui);
        EzpeletaP.agregarMedium(lolo);
        Medium loloP = mediumService.crear(lolo);


        //loloP está en una Ubicacion diferente a la de espiritu2
        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> {
            serviceEspiritu.conectar(espiritu2.getId(), loloP.getId());
        });
    }

    @Test
    void noDeberiaConectarUnEspirituQueNoEstaLibre() {
        Ubicacion bernalM = new Ubicacion("Bernal");
        Ubicacion bernalP = ubicacionService.crear(bernalM);

        Medium juan = new Medium("Juan", 120, 20, berazategui);

        bernalP.agregarMedium(juan);
        Medium juanP = mediumService.crear(juan);

        Espiritu espiritu = serviceEspiritu.crear(matias);
        Espiritu espiritu2 = mediumService.invocar(juanP.getId(), espiritu.getId());

        //ahora espiritu2 está en una Ubicacion
        assertEquals(espiritu2.getUbicacion().getId(), juanP.getUbicacion().getId());

        Medium lolo = new Medium("Lolo", 120, 20, berazategui);
        bernalP.agregarMedium(lolo);
        Medium loloP = mediumService.crear(lolo);


        //loloP está en la misma Ubicacion que espiritu2
        serviceEspiritu.conectar(espiritu2.getId(), loloP.getId());

        Medium pipo = new Medium("Pipo", 120, 20, berazategui);
        bernalP.agregarMedium(pipo);
        Medium pipoP = mediumService.crear(pipo);

        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> {
            serviceEspiritu.conectar(espiritu2.getId(), pipoP.getId());
        });
    }

    @Transactional
    @Test
    public void seConectaUnEspirituConUnMedium() {
        Ubicacion bernalM = new Ubicacion("Bernal");
        Ubicacion bernalP = ubicacionService.crear(bernalM);
        Espiritu juanM = new Angelical("Juan", bernalP);
        Espiritu espirituP = serviceEspiritu.crear(juanM);
        Medium mediumM = new Medium("Juan", 120, 10, berazategui);

        assertEquals(0, espirituP.getNivelDeConexion());
        mediumM.setUbicacion(bernalP);
        Medium mediumP = mediumService.crear(mediumM);
        Medium medium2 = serviceEspiritu.conectar(espirituP.getId(), mediumM.getId());

        assertFalse(medium2.getEspiritus().isEmpty());

        Espiritu espirituActualizado = serviceEspiritu.recuperar(espirituP.getId());

        assertEquals(2.0, espirituActualizado.getNivelDeConexion());
    }

    @Transactional
    @Test
    void deberiaAsociarElEspirituAlMedium() {
        Espiritu nuevoEspiritu = new Angelical("Matias", berazategui);
        Espiritu nuevoEspirituP = serviceEspiritu.crear(nuevoEspiritu);

        Medium mediumJuan = new Medium("Juan", 120, 10, berazategui);

        Medium mediumJuanP = mediumService.crear(mediumJuan);
        ubicacionService.actualizar(berazategui); // antes ubicacionP

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
        Medium medium = new Medium("Juan", 120, 25, berazategui);
        Espiritu nuevoEspiritu = new Angelical("Juan", berazategui);


        ubicacion.agregarMedium(medium);
        ubicacion.agregarEspiritu(nuevoEspiritu);
        Medium mediumP = mediumService.crear(medium);
        Espiritu espirituP = serviceEspiritu.crear(nuevoEspiritu);
        ubicacionService.actualizar(ubicacionP);


        serviceEspiritu.conectar(espirituP.getId(), mediumP.getId());

        Espiritu recuperado = serviceEspiritu.recuperar(espirituP.getId());

        assertEquals(mediumP.cantidadDeFortalecimientoDeConexion(), recuperado.getNivelDeConexion());
    }

    @Transactional
    @Test
    void deberiaPermitirConectarVariosEspiritusAlMismoMedium() {
        Ubicacion ubicacion = new Ubicacion("Tailandia");
        Ubicacion ubiPersistida = ubicacionService.crear(ubicacion);
        Medium medium = new Medium("Juan", 120, 10, berazategui);
        Espiritu pepe = new Angelical("Pepe", berazategui);
        Espiritu raul = new Angelical("Raul", berazategui);
        ubicacion.agregarEspiritu(pepe);
        ubicacion.agregarEspiritu(raul);
        ubicacion.agregarMedium(medium);

        Espiritu espiritu1 = serviceEspiritu.crear(pepe);
        Espiritu espiritu2 = serviceEspiritu.crear(raul);

        Medium mediumP = mediumService.crear(medium);
        ubicacionService.actualizar(ubiPersistida);
        serviceEspiritu.conectar(espiritu1.getId(), mediumP.getId());
        serviceEspiritu.conectar(espiritu2.getId(), mediumP.getId());

        Medium mediumActualizado = mediumService.recuperar(mediumP.getId());
        assertEquals(2, mediumActualizado.getEspiritus().size());
    }


    @Test
    void deberiaMantenerLosDatosOriginalesDelEspirituLuegoDeConectar() {
        Medium medium = new Medium("Juan", 120, 10, berazategui);
        mediumService.crear(medium);
        serviceEspiritu.crear(matias);


        String nombreOriginal = matias.getNombre();

        serviceEspiritu.conectar(matias.getId(), medium.getId());
        Espiritu recuperado = serviceEspiritu.recuperar(matias.getId());

        assertEquals(nombreOriginal, recuperado.getNombre());
        assertEquals(matias.getId(), recuperado.getId());
       

    }

    @AfterEach
    void tearDown() {
        serviceEspiritu.eliminarTodos();
        mediumService.eliminarTodos();
        ubicacionService.eliminarTodos();

    }


}
