package ar.edu.unq.epersgeist.persistencia;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    private void executeBlockInPersistentContext(Runnable aBlock) {
        HibernateTransactionRunner.runTrx(() -> {
                    aBlock.run();
                    return null;
                }
        );
    }

}
