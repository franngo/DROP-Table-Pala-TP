package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import ar.edu.unq.epersgeist.modelo.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class EspirituServiceTest {
    private static final Integer ENERGIA = 100;
    private List<Coordenada> coordenadas;
    private Coordenada coordenadaEspiritu;
    private Coordenada coordenadaMedium;
    private List<Coordenada> coordenadas2;
    private Coordenada coordenadaEspiritu2;
    private Coordenada coordenadaMedium2;

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
        coordenadas = List.of(
                new Coordenada(-34.81, -58.22),
                new Coordenada(-34.82, -58.24),
                new Coordenada(-34.80, -58.20)
        );

        coordenadaEspiritu = new Coordenada(-34.81, -58.22);
        coordenadaMedium = new Coordenada(-34.81, -58.22);

        coordenadas2 = List.of(
                new Coordenada(-34.91, -58.92),
                new Coordenada(-34.92, -58.94),
                new Coordenada(-34.90, -58.90)
        );

        coordenadaEspiritu2 = new Coordenada(-34.91, -58.32);
        coordenadaMedium2 = new Coordenada(-34.91, -58.32);

        berazategui = ubicacionService.crear(new Santuario("Berazategui", ENERGIA), coordenadas);
        matias = new Angelical("Matias", berazategui);
    }

    //crear

    @Test
    void seCreaUnEspirituCorrectamente() {
        Espiritu espirituP = serviceEspiritu.crear(matias, new Coordenada(-34.81, -58.22));

        assertNotNull(espirituP.getId());
    }

    @Test
    void fallaAlCrearEspirituNulo() {
        Espiritu espiritu = null;
        assertThrows(ErrorAlCrearEspirituException.class, () -> {
            serviceEspiritu.crear(espiritu, coordenadaEspiritu);
        });
    }

    @Test
    void noSeCreaUnEspirituPorqueYaTieneIdAsignado() {
        matias.setId(1L);

        assertThrows(ErrorAlCrearEspirituException.class, () -> {
            serviceEspiritu.crear(matias, coordenadaEspiritu);
        });
    }

    //recuperar

    @Test
    void deberiaRecuperarCorrectamenteLosDatosDelEspiritu() {

        Espiritu espirituP = serviceEspiritu.crear(matias, coordenadaEspiritu);
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


        Long id = serviceEspiritu.crear(matias, coordenadaEspiritu).getId();


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

        Long id = serviceEspiritu.crear(matias, coordenadaEspiritu).getId();

        serviceEspiritu.eliminar(id);

        assertThrows(ErrorDeEspirituNoEncontradoException.class, () -> {
            serviceEspiritu.recuperar(id);
        });
    }

    @Test
    void eliminoUnEspirituCorrectamente() {

        Espiritu espirituEliminar = serviceEspiritu.crear(matias, coordenadaEspiritu);

        assertNotNull(espirituEliminar.getId());

        serviceEspiritu.eliminar(espirituEliminar.getId());

        assertThrows(ErrorDeEspirituNoEncontradoException.class, () -> {
            serviceEspiritu.recuperar(espirituEliminar.getId());
        });
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

        serviceEspiritu.crear(modelDaemon, coordenadaEspiritu);
        serviceEspiritu.crear(anotherModelDaemon, coordenadaEspiritu);

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

        Espiritu charizardP = serviceEspiritu.crear(charizard, coordenadaEspiritu);
        Espiritu bulbasaurioP = serviceEspiritu.crear(bulbasaurio, coordenadaEspiritu);
        Espiritu pikuaticoP = serviceEspiritu.crear(pikuatico, coordenadaEspiritu);

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

        serviceEspiritu.crear(modelDaemon, coordenadaEspiritu);
        serviceEspiritu.crear(modelAngelical, coordenadaEspiritu);

        List<Demonio> listOfDemons = serviceEspiritu.espiritusDemoniacos();
        assertFalse(listOfDemons.isEmpty());
        assertEquals(1, listOfDemons.size());
    }

    //espiritusDemoniacos (con paginación)

    @Test
    void losDemoniosSeDevuelvenOrdenadosPorNivelDeConexionDeFormaAscendenteYPaginados() {
        Espiritu charizard = new Demonio("Charizard", berazategui);
        charizard.setNivelDeConexion(95);
        serviceEspiritu.crear(charizard, coordenadaEspiritu);

        Espiritu bulbasaur = new Demonio("bulbasaur", berazategui);
        bulbasaur.setNivelDeConexion(90);
        serviceEspiritu.crear(bulbasaur, coordenadaEspiritu);

        Espiritu pikachu = new Demonio("pikachu", berazategui);
        pikachu.setNivelDeConexion(80);
        serviceEspiritu.crear(pikachu, coordenadaEspiritu);

        Espiritu ghastly = new Demonio("ghastly", berazategui);
        ghastly.setNivelDeConexion(65);
        serviceEspiritu.crear(ghastly, coordenadaEspiritu);

        Espiritu pichu = new Demonio("pichu", berazategui);
        pichu.setNivelDeConexion(55);
        serviceEspiritu.crear(pichu, coordenadaEspiritu);

        Espiritu raichu = new Demonio("raichu", berazategui);
        raichu.setNivelDeConexion(45);
        serviceEspiritu.crear(raichu, coordenadaEspiritu);

        Espiritu magikarp = new Demonio("magikarp", berazategui);
        magikarp.setNivelDeConexion(5);
        serviceEspiritu.crear(magikarp, coordenadaEspiritu);


        Page<Espiritu> daemons = serviceEspiritu.espiritusDemoniacos(Direccion.ASCENDENTE, 1, 3);
        List<Espiritu> demoniosList = daemons.getContent();
        assertEquals(3, demoniosList.size());
        assertEquals(demoniosList.get(0).getId(), magikarp.getId());
        assertEquals(demoniosList.get(1).getId(), raichu.getId());
        assertEquals(demoniosList.get(2).getId(), pichu.getId());

        Page<Espiritu> daemons2 = serviceEspiritu.espiritusDemoniacos(Direccion.ASCENDENTE, 2, 3);
        List<Espiritu> demoniosList2 = daemons2.getContent();
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

        serviceEspiritu.crear(modelDaemon, coordenadaEspiritu);
        serviceEspiritu.crear(modelAngelical, coordenadaEspiritu);

        List<Espiritu> listOfDemons = serviceEspiritu.recuperarTodos();
        assertFalse(listOfDemons.isEmpty());
        assertEquals(2, listOfDemons.size());
    }

    //conectar


    @Test
    void noDeberiaConectarUnEspirituInexistenteConUnMedium() {
        Medium medium = new Medium("Juan", 120, 10, berazategui);
        Medium mediumP = mediumService.crear(medium, coordenadaMedium);

        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> {
            serviceEspiritu.conectar(9999L, mediumP.getId());
        });
    }

    @Test
    void noDeberiaConectarUnEspirituConUnMediumInexistente() {
        Espiritu espiritu = serviceEspiritu.crear(matias, coordenadaEspiritu);

        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> {
            serviceEspiritu.conectar(espiritu.getId(), 9999L);
        });
    }

    @Test
    void noDeberiaConectarUnEspirituEliminado() {
        Espiritu espiritu = serviceEspiritu.crear(matias, coordenadaEspiritu);
        serviceEspiritu.eliminar(espiritu.getId()); //eliminado de la DB, pero el objeto en la VM sigue con su id

        Medium medium = new Medium("Juan", 120, 10, berazategui);
        Medium mediumP = mediumService.crear(medium, coordenadaMedium);

        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> {
            serviceEspiritu.conectar(espiritu.getId(), mediumP.getId());
        });
    }


    @Test
    void noDeberiaConectarUnEspirituQueEstaEnDiferenteUbicacion() {

        Ubicacion bera = ubicacionService.crear(new Santuario("Bera", ENERGIA), coordenadas);

        Ubicacion bernalito = ubicacionService.crear(new Santuario("Bernalito", ENERGIA), coordenadas);

        // ------------------------------------------------------------
        // Medium que invoca al espiritu
        // ------------------------------------------------------------

        Medium juan = new Medium("Juan", 120, 20, bera);

        Medium juanP = mediumService.crear(juan, coordenadaMedium);

        // ------------------------------------------------------------
        // Espiritu invocado en Berazategui
        // ------------------------------------------------------------

        Espiritu espiritu = serviceEspiritu.crear(matias, coordenadaEspiritu);

        Espiritu espirituInvocado = mediumService.invocar(juanP.getId(), espiritu.getId());

        assertEquals(
                bera.getId(),
                espirituInvocado.getUbicacion().getId()
        );

        // ------------------------------------------------------------
        // Otro medium en Bernal
        // ------------------------------------------------------------

        Medium lolo = new Medium("Lolo", 120, 20, bernalito);

        Medium loloP = mediumService.crear(lolo, coordenadaMedium);

        assertEquals(
                bernalito.getId(),
                loloP.getUbicacion().getId()
        );

        // ------------------------------------------------------------
        // Verificacion
        // ------------------------------------------------------------

        assertNotEquals(
                espirituInvocado.getUbicacion().getId(),
                loloP.getUbicacion().getId()
        );

        assertThrows(
                ErrorAlConectarEspirituConMedium.class,
                () -> serviceEspiritu.conectar(
                        espirituInvocado.getId(),
                        loloP.getId()
                )
        );
    }

    @Test
    void noDeberiaConectarUnEspirituQueNoEstaLibre() {
        Ubicacion bernalM = new Santuario("Bernal", ENERGIA);
        Ubicacion bernalP = ubicacionService.crear(bernalM, coordenadas2);

        Medium juan = new Medium("Juan", 120, 20, berazategui);

        Medium juanP = mediumService.crear(juan, coordenadaMedium);

        Espiritu espiritu = serviceEspiritu.crear(matias, coordenadaEspiritu2);
        Espiritu espiritu2 = mediumService.invocar(juanP.getId(), espiritu.getId());

        //ahora espiritu2 está en una Ubicacion
        assertEquals(espiritu2.getUbicacion().getId(), juanP.getUbicacion().getId());

        Medium lolo = new Medium("Lolo", 120, 20, berazategui);
        Medium loloP = mediumService.crear(lolo, coordenadaMedium);


        //loloP está en la misma Ubicacion que espiritu2
        serviceEspiritu.conectar(espiritu2.getId(), loloP.getId());

        Medium pipo = new Medium("Pipo", 120, 20, berazategui);
        Medium pipoP = mediumService.crear(pipo, coordenadaMedium);

        assertThrows(ErrorAlConectarEspirituConMedium.class, () -> {
            serviceEspiritu.conectar(espiritu2.getId(), pipoP.getId());
        });
    }


    @Test
    public void seConectaUnEspirituConUnMedium() {
        Ubicacion bernalM = new Santuario("Bernal", ENERGIA);
        Ubicacion bernalP = ubicacionService.crear(bernalM, coordenadas);
        Espiritu juanM = new Angelical("Juan", bernalP);
        Espiritu espirituP = serviceEspiritu.crear(juanM, coordenadaMedium);
        Medium mediumM = new Medium("Juan", 120, 10, berazategui);

        assertEquals(0, espirituP.getNivelDeConexion());
        mediumM.setUbicacion(bernalP);
        Medium mediumP = mediumService.crear(mediumM, coordenadaMedium);
        Medium medium2 = serviceEspiritu.conectar(espirituP.getId(), mediumM.getId());

        assertFalse(medium2.getEspiritus().isEmpty());

        Espiritu espirituActualizado = serviceEspiritu.recuperar(espirituP.getId());

        assertEquals(2.0, espirituActualizado.getNivelDeConexion());
    }


    @Test
    void deberiaAsociarElEspirituAlMedium() {
        Espiritu nuevoEspiritu = new Angelical("Matias", berazategui);
        Espiritu nuevoEspirituP = serviceEspiritu.crear(nuevoEspiritu, coordenadaEspiritu);

        Medium mediumJuan = new Medium("Juan", 120, 10, berazategui);

        Medium mediumJuanP = mediumService.crear(mediumJuan, coordenadaMedium);
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
        Ubicacion ubicacion = new Santuario("Tailandia", ENERGIA);
        Ubicacion ubicacionP = ubicacionService.crear(ubicacion, coordenadas);
        Medium medium = new Medium("Juan", 120, 25, berazategui);
        Espiritu nuevoEspiritu = new Angelical("Juan", berazategui);

        Medium mediumP = mediumService.crear(medium, coordenadaMedium);
        Espiritu espirituP = serviceEspiritu.crear(nuevoEspiritu, coordenadaEspiritu);
        ubicacionService.actualizar(ubicacionP);


        serviceEspiritu.conectar(espirituP.getId(), mediumP.getId());

        Espiritu recuperado = serviceEspiritu.recuperar(espirituP.getId());

        assertEquals(mediumP.cantidadDeFortalecimientoDeConexion(), recuperado.getNivelDeConexion());
    }

    @Test
    void deberiaPermitirConectarVariosEspiritusAlMismoMedium() {
        Ubicacion ubicacion = new Santuario("Tailandia", ENERGIA);
        Ubicacion ubiPersistida = ubicacionService.crear(ubicacion, coordenadas);
        Medium medium = new Medium("Juan", 120, 10, berazategui);
        Espiritu pepe = new Angelical("Pepe", berazategui);
        Espiritu raul = new Angelical("Raul", berazategui);


        Espiritu espiritu1 = serviceEspiritu.crear(pepe, coordenadaEspiritu);
        Espiritu espiritu2 = serviceEspiritu.crear(raul, coordenadaEspiritu);

        Medium mediumP = mediumService.crear(medium, coordenadaMedium);
        ubicacionService.actualizar(ubiPersistida);
        serviceEspiritu.conectar(espiritu1.getId(), mediumP.getId());
        serviceEspiritu.conectar(espiritu2.getId(), mediumP.getId());

        Medium mediumActualizado = mediumService.recuperar(mediumP.getId());
        assertEquals(2, mediumActualizado.getEspiritus().size());
    }


    @Test
    void deberiaMantenerLosDatosOriginalesDelEspirituLuegoDeConectar() {
        Medium medium = new Medium("Juan", 120, 10, berazategui);
        mediumService.crear(medium, coordenadaMedium);
        serviceEspiritu.crear(matias, coordenadaEspiritu);


        String nombreOriginal = matias.getNombre();

        serviceEspiritu.conectar(matias.getId(), medium.getId());
        Espiritu recuperado = serviceEspiritu.recuperar(matias.getId());

        assertEquals(nombreOriginal, recuperado.getNombre());
        assertEquals(matias.getId(), recuperado.getId());


    }

    // --------------------------------------------------------------------------------
    // dominar()
    // --------------------------------------------------------------------------------

    @Nested
    class Dominar {

        Espiritu matiasP;
        Espiritu pepeP;

        @BeforeEach
        void setUp() {
            matiasP = serviceEspiritu.crear(matias, new Coordenada(-34.81, -58.22));
            pepeP = serviceEspiritu.crear(new Angelical("Pepe", berazategui), new Coordenada(-34.80, -58.20));
        }

        @Test
        void fallSiElDominanteNoEstaPersistido() {
            assertThrows(ErrorDeEspirituNoEncontradoException.class, () -> serviceEspiritu.dominar(999L, pepeP.getId()));
        }

        @Test
        void fallSiElObjetivoNoEstaPersistido() {
            assertThrows(ErrorDeEspirituNoEncontradoException.class, () -> serviceEspiritu.dominar(matiasP.getId(), 999L));
        }

        @Test
        void fallaCuandoElObjetivoLoDomina() {
            serviceEspiritu.dominar(matiasP.getId(), pepeP.getId());
            //en este punto, pepe está dominado por matiasP y no podría dominarlo.
            assertThrows(ErrorDominacionInvalidaCandidatoLoDominaException.class, () -> serviceEspiritu.dominar(pepeP.getId(), matiasP.getId()));
        }

        @Test
        void casoExitoso() {
            //La distancia entre matiasP y pepeP es de 2140.339474411033 metros, por lo que está dentro del rango válido
            //pepeP está libre, su energia es 0 y no está dominado. Tampoco se da que pepeP esté dominando a matiasP.
            //Por tanto, matiasP puede dominar a pepeP
            serviceEspiritu.dominar(matiasP.getId(), pepeP.getId());

            assertTrue(pepeP.estaDominado());
            assertEquals(pepeP.getEspirituDominante().getId(), matiasP.getId());
        }

        @Test
        void unEspirituPuedeDominarAMuchos() {
            Espiritu coquiP = serviceEspiritu.crear(new Angelical("Coqui", berazategui), new Coordenada(-34.80, -58.20));
            Espiritu mariaenemaP = serviceEspiritu.crear(new Angelical("María Enema", berazategui), new Coordenada(-34.80, -58.20));

            //matiasP puede dominar a pepeP, coquiP y mariaenemaP
            serviceEspiritu.dominar(matiasP.getId(), pepeP.getId());
            serviceEspiritu.dominar(matiasP.getId(), coquiP.getId());
            serviceEspiritu.dominar(matiasP.getId(), mariaenemaP.getId());

            assertTrue(pepeP.estaDominado());
            assertEquals(pepeP.getEspirituDominante().getId(), matiasP.getId());
            assertTrue(coquiP.estaDominado());
            assertEquals(coquiP.getEspirituDominante().getId(), matiasP.getId());
            assertTrue(mariaenemaP.estaDominado());
            assertEquals(mariaenemaP.getEspirituDominante().getId(), matiasP.getId());
        }

        @Test
        void unEspirituDominadoPuedeDominarAMuchos() {
            Espiritu coquiP = serviceEspiritu.crear(new Angelical("Coqui", berazategui), new Coordenada(-34.80, -58.23));
            Espiritu mariaenemaP = serviceEspiritu.crear(new Angelical("María Enema", berazategui), new Coordenada(-34.80, -58.23));

            //matiasP puede dominar a pepeP
            serviceEspiritu.dominar(matiasP.getId(), pepeP.getId());

            //pepeP puede dominar a coquiP y a mariaenemaP a pesar de estar siendo dominado por matiasP
            //la distancia entre pepeP y estos dos es de 2.739km, los 2 están libres, su energía es 0 y  no están siendo dominados
            serviceEspiritu.dominar(pepeP.getId(), coquiP.getId());
            serviceEspiritu.dominar(pepeP.getId(), mariaenemaP.getId());

            assertTrue(pepeP.estaDominado());
            assertEquals(pepeP.getEspirituDominante().getId(), matiasP.getId());
            assertTrue(coquiP.estaDominado());
            assertEquals(coquiP.getEspirituDominante().getId(), pepeP.getId());
            assertTrue(mariaenemaP.estaDominado());
            assertEquals(mariaenemaP.getEspirituDominante().getId(), pepeP.getId());
        }

        //probar tmb caso donde a puede dominar varios
        //probar caso donde a domina a b, y b puede dominar a otros excepto a a

        @Test
        void noDeberiaConectarUnEspirituQueEstaSiendoDominado() {

            Espiritu dominante =
                    serviceEspiritu.crear(
                            new Demonio("Dominante", berazategui),
                            coordenadaEspiritu
                    );

            Espiritu dominado =
                    serviceEspiritu.crear(
                            new Angelical("Dominado", berazategui),
                            coordenadaEspiritu
                    );

            dominado.setEspirituDominante(dominante);
            serviceEspiritu.actualizar(dominado);

            Medium medium =
                    mediumService.crear(
                            new Medium("Juan", 120, 10, berazategui),
                            coordenadaMedium
                    );

            assertThrows(
                    EspirituDominadoException.class,
                    () -> serviceEspiritu.conectar(
                            dominado.getId(),
                            medium.getId()
                    )
            );
        }
    }


    @AfterEach
    void tearDown() {
        serviceEspiritu.eliminarTodos();
        mediumService.eliminarTodos();
        ubicacionService.eliminarTodos();

    }


}
