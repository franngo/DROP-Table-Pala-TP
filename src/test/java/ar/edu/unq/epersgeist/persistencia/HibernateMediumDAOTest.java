package ar.edu.unq.epersgeist.persistencia;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
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


    //test recuperar y eliminar
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

}
