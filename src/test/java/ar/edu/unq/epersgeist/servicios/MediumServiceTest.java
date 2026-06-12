package ar.edu.unq.epersgeist.servicios;


import ar.edu.unq.epersgeist.modelo.Coordenada;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Cementerio;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.medium.MediumMongo;
import ar.edu.unq.epersgeist.modelo.random.Estrategia.EstrategiaRangoEspecifico;
import ar.edu.unq.epersgeist.modelo.random.RandomDefensa;
import ar.edu.unq.epersgeist.persistencia.mongo.MediumDAOMongo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private List<Coordenada> coordenadas;
    private Coordenada coordenadaMedium;
    private Coordenada coordenadaEspiritu;


    @BeforeEach
    public void setUp() {
        coordenadas = List.of(
                new Coordenada(-34.81, -58.22),
                new Coordenada(-34.82, -58.24),
                new Coordenada(-34.80, -58.20)
        );

        coordenadaMedium = new Coordenada(-34.812, -58.214);
        coordenadaEspiritu = new Coordenada(-34.815, -58.220);

        berazategui = ubicacionService.crear(new Santuario("Berazategui", ENERGIA), coordenadas);
        bernal = ubicacionService.crear(new Santuario("Bernal", ENERGIA), coordenadas);

        espirituM = new Angelical("Charizard", berazategui);
        espirituM2 = new Angelical("Bulbasor", berazategui);
        epifanioM = new Medium("Epifanio el poderoso", 150, 50, bernal);
        ubedaM = new Medium("Ubeda el dt", 100, 20, bernal);

        epifanioP = serviceMedium.crear(epifanioM, coordenadaMedium);
        ubedaP = serviceMedium.crear(ubedaM, coordenadaMedium);

        rikaM = new Angelical("Rika", bernal);
        rikaM.setNivelDeConexion(60);
        rikaP = espirituService.crear(rikaM, coordenadaEspiritu);

        jaegerM = new Demonio("Jaeger", bernal);
        jaegerM.setNivelDeConexion(50);
        jaegerP = espirituService.crear(jaegerM, coordenadaEspiritu);
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
            Medium mediumP = serviceMedium.crear(null, new Coordenada(-0.0, -0.0));
        });
    }

    @Test
    void noSeCreaUnMediumPorqueYaTieneIdAsignado() {
        Medium matiasM = new Medium("Matias", 150, 50, berazategui);
        matiasM.setId(1L);

        assertThrows(ErrorAlCrearMediumException.class, () -> {
            serviceMedium.crear(matiasM, new Coordenada(-0.0, -0.0));
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

        Long id = serviceMedium.crear(mediumPrueba, coordenadaMedium).getId();

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
        Long id = serviceMedium.crear(mediumPrueba, coordenadaMedium).getId();

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
        Long id = serviceMedium.crear(mediumPrueba, coordenadaMedium).getId();

        serviceMedium.eliminar(id);

        assertThrows(ErrorDeMediumNoEncontradoException.class, () -> {
            serviceMedium.recuperar(id);
        });
    }

    //descansar
    @Test
    public void descansarCasoExitoso() {

        Medium mediumPrueba = new Medium("MediumTest", 200, 50, bernal);
        Medium mediumPruebaP = serviceMedium.crear(mediumPrueba, coordenadaMedium);

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
        Espiritu espirituP = espirituService.crear(espirituM, coordenadaEspiritu);

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
        Medium mediumPruebaP = serviceMedium.crear(mediumPrueba, coordenadaMedium);

        Long idInexistente = mediumPruebaP.getId() + 1;
        ErrorDeEspirituNoEncontradoException exception = assertThrows(ErrorDeEspirituNoEncontradoException.class,
                () -> serviceMedium.invocar(mediumPruebaP.getId(), idInexistente));
        assertEquals("No se pudo recuperar al espíritu con id " + idInexistente + ".", exception.getMessage());
    }

    //exorcizar

    @Test
    void exorcismoNoExitosoDeberiaAfectarElAngelDelMedium() {
        // Por una cuestion de comodidad voy a crear el contexto con nuevas variables
        Ubicacion quilmes = ubicacionService.crear(new Santuario("Quilmes", ENERGIA), coordenadas);

        Medium matiasM = new Medium("Matias", 150, 50, quilmes);

        Medium francoM = new Medium("Franco", 100, 20, quilmes);

        Medium matiasP = serviceMedium.crear(matiasM, coordenadaMedium);
        Medium francoP = serviceMedium.crear(francoM, coordenadaMedium);

        Espiritu omnimanM = new Angelical("Omniman", quilmes);
        omnimanM.setNivelDeConexion(60);
        Espiritu omnimanP = espirituService.crear(omnimanM, coordenadaEspiritu);

        Espiritu markM = new Demonio("Mark", quilmes);
        markM.setNivelDeConexion(50);
        Espiritu markP = espirituService.crear(markM, coordenadaEspiritu);
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
        Ubicacion quilmes = ubicacionService.crear(new Santuario("Quilmes", ENERGIA), coordenadas);

        Medium matiasM = new Medium("Matias", 150, 50, quilmes);

        Medium matiasP = serviceMedium.crear(matiasM, coordenadaMedium);

        assertThrows(ExorcismoPropioNoPermitidoException.class, () -> serviceMedium.exorcizar(matiasP.getId(), matiasP.getId()));
    }

    @Test
    void unMediumNoPuedeExorcizarAOtroEnUbicacionDiferente() {
        // Por una cuestion de comodidad voy a crear el contexto con nuevas variables
        Ubicacion quilmes = ubicacionService.crear(new Santuario("Quilmes", ENERGIA), coordenadas);
        Ubicacion wilde = ubicacionService.crear(new Santuario("Wilde", ENERGIA), coordenadas);

        Medium matiasM = new Medium("Matias", 150, 50, quilmes);

        Medium francoM = new Medium("Franco", 100, 20, wilde);

        Medium matiasP = serviceMedium.crear(matiasM, coordenadaMedium);
        Medium francoP = serviceMedium.crear(francoM, coordenadaMedium);

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


    @Nested
    public class MoverConActualizacionDeUbicaciones {

        Ubicacion ranelagh;
        Ubicacion sourigues;
        Medium mediumAMover;

        @BeforeEach
        void setUtMover() {
            List<Coordenada> coordsRanelagh = List.of(
                    new Coordenada(-34.80, -58.20),
                    new Coordenada(-34.80, -58.18),
                    new Coordenada(-34.78, -58.18),
                    new Coordenada(-34.78, -58.20)
            );

            List<Coordenada> coordsSourigues = List.of(
                    new Coordenada(-34.85, -58.25),
                    new Coordenada(-34.85, -58.23),
                    new Coordenada(-34.83, -58.23),
                    new Coordenada(-34.83, -58.25)
            );

            ranelagh = ubicacionService.crear(new Santuario("Ranelagh", 99), coordsRanelagh);
            sourigues = ubicacionService.crear(new Santuario("Sourigues", 70), coordsSourigues);
            ubicacionService.conectar(ranelagh.getId(), sourigues.getId(), 40L);
            mediumAMover = serviceMedium.crear(new Medium("Matias Sanchez", 100, 90, ranelagh), coordenadaMedium);

            // espiritus que acompañanan a
            Espiritu elChiqui = espirituService.crear(new Angelical("Chiqui Tapia", ranelagh), coordenadaEspiritu);
            Espiritu juanchiElPastelero = espirituService.crear(new Angelical("Juanchito", ranelagh), coordenadaEspiritu);

            espirituService.conectar(elChiqui.getId(), mediumAMover.getId());
            espirituService.conectar(juanchiElPastelero.getId(), mediumAMover.getId());
        }

        @Test
        void elMediumSeMueveExitosamenteALaUbicacionDada() {
            serviceMedium.mover(mediumAMover.getId(), -34.84, -58.24);
            Medium recuperado = serviceMedium.recuperar(mediumAMover.getId());
            assertThat(sourigues.getId()).isEqualTo(recuperado.getUbicacion().getId());
        }

        @Test
        void elMediumPierdeManaConcistentementeConElCosto() {
            serviceMedium.mover(mediumAMover.getId(), -34.84, -58.24);
            Medium recuperado = serviceMedium.recuperar(mediumAMover.getId());

            assertThat(50).isEqualTo(recuperado.getMana());

        }

        @Test
        void elMediumSeMueveAlMismoLugarSinEfecto() {
            // estado actual del medium
            assertEquals(ranelagh.getId(), mediumAMover.getUbicacion().getId());
            assertEquals(90, mediumAMover.getMana());

            serviceMedium.mover(mediumAMover.getId(), -34.79, -58.19);
            Medium recuperado = serviceMedium.recuperar(mediumAMover.getId());

            assertThat(ranelagh.getId()).isEqualTo(recuperado.getUbicacion().getId());
            assertThat(90).isEqualTo(recuperado.getMana());
        }

        @Test
        void elMediumNoPuedeMoverseAUnaUbicacionLejana() {
            List<Coordenada> coordsVarela = List.of(
                    new Coordenada(-35.20, -58.70),
                    new Coordenada(-35.20, -58.68),
                    new Coordenada(-35.18, -58.68),
                    new Coordenada(-35.18, -58.70)
            );
            Ubicacion varela = ubicacionService.crear(new Cementerio("Varela", 50), coordsVarela);
            ubicacionService.conectar(sourigues.getId(), varela.getId(), 30L);

            assertThatThrownBy(
                    () -> serviceMedium.mover(mediumAMover.getId(), -35.19, -58.69))
                    .isInstanceOf(UbicacionLejanaException.class);

        }

        @Test
        void elMediumNoPuedeMoverseAUnaUbicacionInexistente() {

            assertThatThrownBy(() ->
                    serviceMedium.mover(
                            mediumAMover.getId(),
                            -50.0,
                            -90.0
                    )
            ).isInstanceOf(CoordenadasSinUbicacion.class);
        }

        @Test
        void elMediumSiQuedaSinManaEsEliminado() {
            List<Coordenada> coordenadasLaUnq = List.of(
                    new Coordenada(-34.81, -58.22),
                    new Coordenada(-34.82, -58.24),
                    new Coordenada(-34.80, -58.20)
            );
            Ubicacion laUNQ =
                    ubicacionService.crear(
                            new Santuario("La Unq", 80),
                            coordenadasLaUnq
                    );

            Medium mediumMover = serviceMedium.crear(
                    new Medium("Juan", 100, 50, ranelagh),
                    new Coordenada(-34.79, -58.19)
            );

            ubicacionService.conectar(
                    ranelagh.getId(),
                    laUNQ.getId(),
                    99L
            );


            serviceMedium.mover(
                    mediumMover.getId(), -34.79, -58.19
            );

            assertThatThrownBy(() ->
                    serviceMedium.recuperar(mediumMover.getId())
            ).isInstanceOf(ErrorDeMediumNoEncontradoException.class);

        }

        @Test
        void losEspiritusDelMediumSeMuevenConEl() {

            serviceMedium.mover(mediumAMover.getId(), -34.79, -58.19);

            List<Espiritu> espiritus = serviceMedium.espiritus(mediumAMover.getId());

            assertThat(espiritus)
                    .extracting(e -> e.getUbicacion().getId())
                    .containsOnly(ranelagh.getId());
        }

        @Test
        void losEspiritusNoCambianDeUbicacionSielMediumSeMuevenALaMisma() {

            Long idUbiActual = mediumAMover.getUbicacion().getId();
            serviceMedium.mover(mediumAMover.getId(), -34.79, -58.19);

            List<Espiritu> espiritus = serviceMedium.espiritus(mediumAMover.getId());

            assertThat(espiritus)
                    .extracting(e -> e.getUbicacion().getId())
                    .containsOnly(idUbiActual);

        }

        @Test
        void elMediumActualizaSuPosicionAlMoverse() {

            Espiritu espirituLejano =
                    espirituService.crear(
                            new Angelical("Pedro", sourigues),
                            new Coordenada(-34.84, -58.24)
                    );

            // Antes de moverse no debería poder invocarlo
            assertThrows(
                    EspirituMuyLejanoException.class,
                    () -> serviceMedium.invocar(
                            mediumAMover.getId(),
                            espirituLejano.getId()
                    )
            );

            serviceMedium.mover(
                    mediumAMover.getId(),
                    -34.80, -58.20
            );

            assertDoesNotThrow(
                    () -> serviceMedium.invocar(
                            mediumAMover.getId(),
                            espirituLejano.getId()
                    )
            );
        }

        @Test
        void elMediumNoPuedeMoverseSiLaDistanciaEsMayorA30Km() {

            List<Coordenada> coordsLejanas = List.of(
                    new Coordenada(-35.50, -58.50),
                    new Coordenada(-35.50, -58.45),
                    new Coordenada(-35.45, -58.45),
                    new Coordenada(-35.45, -58.50)
            );

            Ubicacion lejano =
                    ubicacionService.crear(
                            new Santuario("Lejano", 50),
                            coordsLejanas
                    );

            ubicacionService.conectar(
                    ranelagh.getId(),
                    lejano.getId(),
                    10L
            );

            assertThrows(
                    UbicacionLejanaException.class,
                    () -> serviceMedium.mover(
                            mediumAMover.getId(),
                            -35.48,
                            -58.47
                    )
            );
        }

        @Test
        void losEspiritusSeMuevenJuntoAlMedium() {

            Double nuevaLat = -34.84;
            Double nuevaLon = -58.24;

            // espíritu cerca del destino
            Espiritu espirituCercano =
                    espirituService.crear(
                            new Angelical("Pedro", sourigues),
                            new Coordenada(nuevaLat, nuevaLon)
                    );

            assertThrows(
                    EspirituMuyLejanoException.class,
                    () -> serviceMedium.invocar(
                            mediumAMover.getId(),
                            espirituCercano.getId()
                    )
            );

            serviceMedium.mover(
                    mediumAMover.getId(),
                    nuevaLat,
                    nuevaLon
            );

            assertDoesNotThrow(
                    () -> serviceMedium.invocar(
                            mediumAMover.getId(),
                            espirituCercano.getId()
                    )
            );
        }

        @Test
        void mediumYEspiritusSiguenJuntosDespuesDeMoverse() {

            Double nuevaLat = -34.84;
            Double nuevaLon = -58.24;

            serviceMedium.mover(
                    mediumAMover.getId(),
                    nuevaLat,
                    nuevaLon
            );

            Espiritu espirituDestino =
                    espirituService.crear(
                            new Angelical("Matias", sourigues),
                            new Coordenada(nuevaLat, nuevaLon)
                    );


            assertDoesNotThrow(
                    () -> serviceMedium.invocar(
                            mediumAMover.getId(),
                            espirituDestino.getId()
                    )
            );

            List<Espiritu> espiritus =
                    serviceMedium.espiritus(mediumAMover.getId());

            assertThat(espiritus)
                    .extracting(e -> e.getUbicacion().getId())
                    .containsOnly(sourigues.getId());
        }

        @AfterEach
        void tearDown() {
            espirituService.eliminarTodos();
            serviceMedium.eliminarTodos();
            ubicacionService.eliminarTodos();

        }
    }

    @Nested
    class InvocarConDistancia {

        @Autowired
        private MediumDAOMongo mediumDAOMongo;

        @Test
        void invocaCorrectamenteSiElEspirituEstaAMenosDe50Km() {

            Medium medium = new Medium("Juan", 120, 20, berazategui);
            Medium mediumP = serviceMedium.crear(medium, coordenadaMedium);

            Espiritu espiritu = new Angelical("Matias", berazategui);
            Espiritu espirituP = espirituService.crear(espiritu, coordenadaEspiritu);

            Espiritu invocado =
                    serviceMedium.invocar(mediumP.getId(), espirituP.getId());

            assertNotNull(invocado);
            assertEquals(
                    mediumP.getUbicacion().getId(),
                    invocado.getUbicacion().getId()
            );
        }

        @Test
        void fallaSiElEspirituEstaAMasDe50Km() {

            Coordenada coordenadaLejana =
                    new Coordenada(-35.50, -58.22);

            Medium medium = new Medium("Juan", 120, 20, berazategui);
            Medium mediumP = serviceMedium.crear(medium, coordenadaMedium);

            Espiritu espiritu = new Angelical("Matias", berazategui);
            Espiritu espirituP =
                    espirituService.crear(espiritu, coordenadaLejana);

            assertThrows(
                    EspirituMuyLejanoException.class,
                    () -> serviceMedium.invocar(
                            mediumP.getId(),
                            espirituP.getId()
                    )
            );
        }

        @Test
        void fallaSiElEspirituNoExiste() {

            Medium medium = new Medium("Juan", 120, 20, berazategui);
            Medium mediumP = serviceMedium.crear(medium, coordenadaMedium);

            assertThrows(
                    ErrorDeEspirituNoEncontradoException.class,
                    () -> serviceMedium.invocar(
                            mediumP.getId(),
                            99999L
                    )
            );
        }

        @Test
        void fallaSiElMediumNoExiste() {

            Espiritu espiritu = new Angelical("Matias", berazategui);
            Espiritu espirituP =
                    espirituService.crear(espiritu, coordenadaEspiritu);

            assertThrows(
                    ErrorDeMediumNoEncontradoException.class,
                    () -> serviceMedium.invocar(
                            99999L,
                            espirituP.getId()
                    )
            );
        }

        @Test
        void fallaSiElMediumExisteEnSqlPeroNoEnMongo() {

            Medium medium = new Medium("Juan", 120, 20, berazategui);
            Medium mediumP = serviceMedium.crear(medium, coordenadaMedium);

            Espiritu espiritu = new Angelical("Matias", berazategui);
            Espiritu espirituP =
                    espirituService.crear(espiritu, coordenadaEspiritu);

            mediumDAOMongo.deleteById(mediumP.getId());

            assertThrows(
                    ErrorDeMediumNoEncontradoException.class,
                    () -> serviceMedium.invocar(
                            mediumP.getId(),
                            espirituP.getId()
                    )
            );
        }

        @AfterEach
        void tearDown() {
            espirituService.eliminarTodos();
            serviceMedium.eliminarTodos();
            ubicacionService.eliminarTodos();
        }
    }


    @AfterEach
    void tearDown() {
        espirituService.eliminarTodos();
        serviceMedium.eliminarTodos();
        ubicacionService.eliminarTodos();

    }


}
