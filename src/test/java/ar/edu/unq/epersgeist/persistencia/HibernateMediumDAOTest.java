package ar.edu.unq.epersgeist.persistencia;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlActualizarMediumException;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateMediumDAOTest {

    MediumDAO mediumDao = new HibernateMediumDAO();
    Medium mediumM;

    @BeforeEach
    void setUp() {
        mediumM = new Medium("Matias", 200, 100);
    }

    //tests de crear y actualizar
    @Test
    public void seCreaElMediumEnLaBase() {

        Medium medium = HibernateTransactionRunner.runTrx(() ->
                mediumDao.crear(mediumM)
        );

        assertNotNull(medium.getId());

    }

    @Test
    void deberiaActualizarSoloLosCamposModificados1() {

        Long id = HibernateTransactionRunner.runTrx(() -> mediumDao.crear(mediumM).getId());

        Medium modificado = new Medium("Rolando el audaz", 120, 50);
        modificado.setId(id);

        //como modificado tiene el mismo id que mediumM, actualiza dicha fila en la BD
        this.executeBlockInPersistentContext(() -> mediumDao.actualizar(modificado));

        Medium actualizado = HibernateTransactionRunner.runTrx(() -> mediumDao.recuperar(id));

        assertEquals(modificado.getId(), actualizado.getId());
        assertEquals(modificado.getNombre(), actualizado.getNombre());
        assertEquals(modificado.getMana(), actualizado.getMana());
        assertEquals(modificado.getManaMax(), actualizado.getManaMax());
    }

    @Test
    void deberiaActualizarSoloLosCamposModificados2() {

        Long id = HibernateTransactionRunner.runTrx(() -> mediumDao.crear(mediumM).getId());

        mediumM.setNombre("Rolando el audaz");
        mediumM.setMana(120);


        //se actualiza la fila con el id de epifanio en la DB
        this.executeBlockInPersistentContext(() -> mediumDao.actualizar(mediumM));

        Medium actualizado = HibernateTransactionRunner.runTrx(() -> mediumDao.recuperar(id));

        assertEquals(mediumM.getId(), actualizado.getId());
        assertEquals(mediumM.getNombre(), actualizado.getNombre());
        assertEquals(mediumM.getMana(), actualizado.getMana());
        assertEquals(mediumM.getManaMax(), actualizado.getManaMax());
    }

    @Test
    void noDeberiaActualizarConIdQueNoSeEncuentraEnBD() {
        ErrorAlActualizarMediumException exception =
        assertThrows(ErrorAlActualizarMediumException.class, () -> {
            this.executeBlockInPersistentContext(() -> {
                mediumM.setId(1L); //no hay nada en la DB con id 1, porque arranca vacía
                mediumDao.actualizar(mediumM);
            });
        });
        assertEquals("No existe en la BD el medium con id: 1", exception.getMessage());
    }

    @Test
    void noDeberiaActualizarConIdNulo() {
        ErrorAlActualizarMediumException exception =
        assertThrows(ErrorAlActualizarMediumException.class, () -> {
            this.executeBlockInPersistentContext(() -> {
                mediumDao.actualizar(mediumM); //comienza sin id
            });
        });
        assertEquals("No se puede actualizar un medium que no tiene id.", exception.getMessage());
    }

    //tests de recuperar y eliminar
    @Test
    public void seRecuperaUnMediumDadoUnId() {
        this.executeBlockInPersistentContext(() -> {
            Medium mediumPersistido = mediumDao.crear(mediumM);
            assertNotNull(mediumPersistido.getId());
            Medium mediumRecuperado = mediumDao.recuperar(mediumPersistido.getId());
            assertNotNull(mediumRecuperado.getId());
        });
    }

    @Test
    public void seEliminaUnMediumExistente() {

        this.executeBlockInPersistentContext(() -> {
            Medium mediumPersistido = mediumDao.crear(mediumM);

            mediumDao.eliminar(mediumPersistido.getId());

            assertNull(mediumDao.recuperar(mediumPersistido.getId()));
        });
    }

    @Test
    public void noFallaAlEliminarUnMediumInexistente() {

        Long idInexistente = 999L;

        assertDoesNotThrow(() -> {
                    this.executeBlockInPersistentContext(() -> {
                        mediumDao.eliminar(idInexistente);
                    })
                    ;
                }
        );

    }

    @Test
    public void recuperarTodosDevuelveListaVacia() {

        this.executeBlockInPersistentContext(() -> {
            List<Medium> coleccionSinMediums = mediumDao.recuperarTodos();

            assertNotNull(coleccionSinMediums);
            assertTrue(coleccionSinMediums.isEmpty());
        });
    }

    @Test
    public void recuperarTodosDevuelveUnEspiritu() {

        this.executeBlockInPersistentContext(() -> {
            mediumDao.crear(new Medium("Juanchi el pastelero", 100, 200));

            List<Medium> lista = mediumDao.recuperarTodos();

            assertEquals(1, lista.size());
        });
    }

    @Test
    public void recuperarTodosDevuelveVariosElementos() {

        this.executeBlockInPersistentContext(() -> {

            Medium mediumM1 = new Medium("Juancho", 100, 30);
            Medium mediumM2 = new Medium("Alfonso", 200, 10);
            mediumDao.crear(mediumM1);
            mediumDao.crear(mediumM2);

            List<Medium> lista = mediumDao.recuperarTodos();

            assertEquals(2, lista.size());
        });
    }

    @Test
    public void recuperarTodosDevuelveListaOrdenadaPorNombre() {

        this.executeBlockInPersistentContext(() -> {

            Medium mediumM1 = new Medium("Juancho", 100, 30);
            Medium mediumM2 = new Medium("Alfonso", 200, 10);
            mediumDao.crear(mediumM1);
            mediumDao.crear(mediumM2);


            List<Medium> lista = mediumDao.recuperarTodos();

            assertEquals("Alfonso", lista.get(0).getNombre());
            assertEquals("Juancho", lista.get(1).getNombre());

        });
    }

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
            mediumDao.eliminarTodos();
        });
    }

}
