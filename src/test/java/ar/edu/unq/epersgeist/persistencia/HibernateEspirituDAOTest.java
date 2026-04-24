package ar.edu.unq.epersgeist.persistencia;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlActualizarEspirituException;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateEspirituDAOTest {
    private EspirituDAO espirituDao = new HibernateEspirituDAO();
    private Espiritu espirituM;

    @BeforeEach
    public void setUp() {
        espirituM = new Espiritu("das");
    }

    @Test
    public void seCreaElEspirituEnLaBase() {

        this.executeBlockInPersistentContext(() -> {
            Espiritu espiritu = espirituDao.crear(espirituM);
            assertNotNull(espiritu.getId());
        });

    }


    @Test
    public void seRecuperaUnEspirituDadoUnId() {
        this.executeBlockInPersistentContext(() -> {
            Espiritu espirituPersistido = espirituDao.crear(espirituM);
            assertNotNull(espirituPersistido.getId());
            Espiritu espirituRecuperado = espirituDao.recuperar(espirituPersistido.getId());
            assertNotNull(espirituRecuperado.getId());
        });
    }

    @Test
    public void seEliminaUnEspirituExistente() {

        this.executeBlockInPersistentContext(() -> {
            Espiritu espirituPersistido = espirituDao.crear(espirituM);

            espirituDao.eliminar(espirituPersistido.getId());

            assertNull(espirituDao.recuperar(espirituPersistido.getId()));
        });
    }

    @Test
    public void noFallaAlEliminarUnEspirituInexistente() {

        Long idInexistente = 999L;

        assertDoesNotThrow(() -> {
                    this.executeBlockInPersistentContext(() -> {
                        espirituDao.eliminar(idInexistente);
                    })
                    ;
                }
        );

    }

    @Test
    public void recuperarTodosDevuelveListaVacia() {

        this.executeBlockInPersistentContext(() -> {
            List<Espiritu> coleccionSinEspiritus = espirituDao.recuperarTodos();

            assertNotNull(coleccionSinEspiritus);
            assertTrue(coleccionSinEspiritus.isEmpty());
        });
    }

    @Test
    public void recuperarTodosDevuelveUnEspiritu() {

        this.executeBlockInPersistentContext(() -> {
            espirituDao.crear(new Espiritu("Matias"));

            List<Espiritu> lista = espirituDao.recuperarTodos();

            assertEquals(1, lista.size());
        });
    }

    @Test
    public void recuperarTodosDevuelveVariosElementos() {

        this.executeBlockInPersistentContext(() -> {
            espirituDao.crear(new Espiritu("Matias"));
            espirituDao.crear(new Espiritu("Juan"));

            List<Espiritu> lista = espirituDao.recuperarTodos();

            assertEquals(2, lista.size());
        });
    }

    @Test
    public void recuperarTodosDevuelveListaOrdenadaPorNombre() {

        this.executeBlockInPersistentContext(() -> {
            espirituDao.crear(new Espiritu("Matias"));
            espirituDao.crear(new Espiritu("Juanchi el Pastelero"));
            espirituDao.crear(new Espiritu("Alberto"));

            List<Espiritu> lista = espirituDao.recuperarTodos();

            assertEquals("Alberto", lista.get(0).getNombre());
            assertEquals("Juanchi el Pastelero", lista.get(1).getNombre());
            assertEquals("Matias", lista.get(2).getNombre());
        });
    }

    @Test
    public void seActualizaUnEspirituExistente() {

        this.executeBlockInPersistentContext(() -> {
            Espiritu creado = espirituDao.crear(new Espiritu("Matias"));
            assertNotNull(creado.getId());

            creado.setNombre("NombreActualizado");

            espirituDao.actualizar(creado);

            Espiritu actualizado = espirituDao.recuperar(creado.getId());

            assertEquals("NombreActualizado", actualizado.getNombre());
        });
    }

    @Test
    public void fallaAlActualizarUnEspirituInexistente() {

        Long idInexistente = 999L;
        Espiritu inexistente = new Angelical("NoExisto");

        inexistente.setId(idInexistente);
        ErrorAlActualizarEspirituException exception =
                assertThrows(ErrorAlActualizarEspirituException.class, () -> {
                    this.executeBlockInPersistentContext(() -> {
                        espirituDao.actualizar(inexistente);
                    });
                });
        assertEquals("No existe el espiritu con id: 999", exception.getMessage());
    }

    @Test
    public void fallaAlActualizarUnEspirituSinId() {

        Espiritu espirituNoPersistido = new Angelical("NoEstoyPersistido");

        ErrorAlActualizarEspirituException exception =
                assertThrows(ErrorAlActualizarEspirituException.class, () -> {
                    this.executeBlockInPersistentContext(() -> {
                        espirituDao.actualizar(espirituNoPersistido);
                    });
                });
        assertEquals("No se puede actualizar un espiritu sin id", exception.getMessage());
    }

    // Support message
    private void executeBlockInPersistentContext(Runnable aBlock) {
        HibernateTransactionRunner.runTrx(() -> {
                    aBlock.run();
                    return null;
                }
        );
    }

    @AfterEach
    void tearDown() {
        executeBlockInPersistentContext(() -> {
            espirituDao.eliminarTodos();
        });
    }
}
