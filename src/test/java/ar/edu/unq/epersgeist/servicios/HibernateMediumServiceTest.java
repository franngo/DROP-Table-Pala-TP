package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlActualizarMediumException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlRecuperarMediumException;
import ar.edu.unq.epersgeist.modelo.exceptions.ExorcismoPropioNoPermitidoException;
import ar.edu.unq.epersgeist.modelo.random.Estrategia.EstrategiaRangoEspecifico;
import ar.edu.unq.epersgeist.modelo.random.RandomDefensa;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateMediumServiceTest {
    private final HibernateMediumDAO mediumDao = new HibernateMediumDAO();
    private final MediumServiceImpl serviceMedium = new MediumServiceImpl(mediumDao);
    private Medium epifanio;
    private Medium ubeda;

    Angelical rika;
    Demonio jaeger;

    @BeforeEach
    public void setUp() {
        epifanio = new Medium("Epifanio el poderoso", 150, 50);
        ubeda = new Medium("Ubeda el dt", 100, 20);

        rika = new Angelical("Angel", "Rika");
        rika.setNivelDeConexion(60);

        jaeger = new Demonio("Demonio", "Jaeger");
        jaeger.setNivelDeConexion(50);

    }

    @Test
    void deberiaAsignarIdAlCrearUnEspiritu() {
        Medium medium = HibernateTransactionRunner.runTrx(() ->
                serviceMedium.crear(epifanio)
        );

        assertNotNull(medium.getId());
    }


    @Test
    void deberiaRecuperarCorrectamenteLosDatosDelEspiritu() {
        Medium mediumRecuperado = HibernateTransactionRunner.runTrx(() -> {
            Medium mediumEpi = serviceMedium.crear(epifanio);
            return serviceMedium.recuperar(mediumEpi.getId());
        });

        assertEquals(epifanio.getId(), mediumRecuperado.getId());
        assertEquals(epifanio.getNombre(), mediumRecuperado.getNombre());
        assertEquals(epifanio.getMana(), mediumRecuperado.getMana());
        assertEquals(epifanio.getManaMax(), mediumRecuperado.getManaMax());
        assertEquals(epifanio.getCantFortalece(), mediumRecuperado.getCantFortalece());
        assertEquals(0, epifanio.getEspiritus().size());
        assertEquals(0, mediumRecuperado.getEspiritus().size());
    }


    @Test
    void deberiaActualizarSoloLosCamposModificados1() {

        Long id = HibernateTransactionRunner.runTrx(() -> serviceMedium.crear(epifanio).getId());

        Medium modificado = new Medium("Rolando el audaz", 120, 50);
        modificado.setId(id);
        modificado.setCantFortalece(15);

        //como modificado tiene el mismo id que epifanio, actualiza dicha fila en la BD
        this.executeBlockInPersistentContext(() -> serviceMedium.actualizar(modificado));

        Medium actualizado = HibernateTransactionRunner.runTrx(() -> serviceMedium.recuperar(id));

        assertEquals(modificado.getId(), actualizado.getId());
        assertEquals(modificado.getNombre(), actualizado.getNombre());
        assertEquals(modificado.getMana(), actualizado.getMana());
        assertEquals(modificado.getManaMax(), actualizado.getManaMax());
        assertEquals(modificado.getCantFortalece(), actualizado.getCantFortalece());
    }

    @Test
    void deberiaActualizarSoloLosCamposModificados2() {

        Long id = HibernateTransactionRunner.runTrx(() -> serviceMedium.crear(epifanio).getId());

        epifanio.setNombre("Rolando el audaz");
        epifanio.setMana(120);
        epifanio.setCantFortalece(15);

        //se actualiza la fila con el id de epifanio en la DB
        this.executeBlockInPersistentContext(() -> serviceMedium.actualizar(epifanio));

        Medium actualizado = HibernateTransactionRunner.runTrx(() -> serviceMedium.recuperar(id));

        assertEquals(epifanio.getId(), actualizado.getId());
        assertEquals(epifanio.getNombre(), actualizado.getNombre());
        assertEquals(epifanio.getMana(), actualizado.getMana());
        assertEquals(epifanio.getManaMax(), actualizado.getManaMax());
        assertEquals(epifanio.getCantFortalece(), actualizado.getCantFortalece());
    }

    @Test
    void noDeberiaActualizarConIdNulo() {
        ErrorAlActualizarMediumException exception =
                assertThrows(ErrorAlActualizarMediumException.class, () -> {
                    HibernateTransactionRunner.runTrx(() -> {
                        serviceMedium.actualizar(epifanio); //comienza sin id
                        return null;
                    });
                });
        assertEquals("No se puede actualizar un medium que no tiene id.", exception.getMessage());
    }

    @Test
    void noDeberiaActualizarConIdQueNoSeEncuentraEnBD() {
        ErrorAlActualizarMediumException exception =
                assertThrows(ErrorAlActualizarMediumException.class, () -> {
                    HibernateTransactionRunner.runTrx(() -> {
                        epifanio.setId(1L); //no hay nada en la DB con id 1, porque arranca vacía
                        serviceMedium.actualizar(epifanio);
                        return null;
                    });
                });
        assertEquals("No existe en la BD el medium con id: 1", exception.getMessage());
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

        Long id = HibernateTransactionRunner.runTrx(() -> serviceMedium.crear(ubeda).getId());

        this.executeBlockInPersistentContext(() -> serviceMedium.eliminar(id));

        assertThrows(ErrorAlRecuperarMediumException.class, () -> {
            serviceMedium.recuperar(id);
        });
    }

    @Test
    void exorcismoNoExitosoDeberiaAfectarElAngelDelMedium() throws ExorcismoPropioNoPermitidoException {
        //setteo el porcentaje de defensa para que el demonio siempre gane
        RandomDefensa.setEstrategiaNumero(new EstrategiaRangoEspecifico(99, 99));

        //creo los medium en la base, para luego agregarles espiritus y actualizarlos
        Medium mediumExorcizador = HibernateTransactionRunner.runTrx(() -> serviceMedium.crear(ubeda));
        Medium mediumAtacado = HibernateTransactionRunner.runTrx(() -> serviceMedium.crear(epifanio));
        mediumExorcizador.agregarEspiritu(rika);
        mediumAtacado.agregarEspiritu(jaeger);

        //actualizo los medium con sus nuevos espiritus
        this.executeBlockInPersistentContext(() -> serviceMedium.actualizar(mediumExorcizador));
        this.executeBlockInPersistentContext(() -> serviceMedium.actualizar(mediumAtacado));

        //realizo exorcismo en el service

        this.executeBlockInPersistentContext(() -> {
            try {
                serviceMedium.exorcizar(mediumExorcizador.getId(), mediumAtacado.getId());
            } catch (ExorcismoPropioNoPermitidoException e) {
                throw new RuntimeException(e);
            }
        });

        // en el exorcismo el mediumExorcizador perdio la batalla, su espiritu deberia reflejar lso cambios
        List<Espiritu> espiritusDelExorcizador = HibernateTransactionRunner.runTrx(() -> serviceMedium.espiritus(mediumExorcizador.getId()));
        Espiritu espirituRika = espiritusDelExorcizador.stream()
                .filter(a -> a.getNombre().equals("Rika"))
                .findFirst()
                .orElse(null);

        //perdio 5 de conexion
        assertNotNull(espirituRika);
        assertEquals(55, espirituRika.getNivelDeConexion());


    }

    private void executeBlockInPersistentContext(Runnable block) {
        HibernateTransactionRunner.runTrx(() -> {
            block.run();
            return null;
        });
    }

    @AfterEach
    void tearDown() {
        HibernateTransactionRunner.runTrx(() -> {
            mediumDao.eliminarTodos();
            return null;
        });
    }
}
