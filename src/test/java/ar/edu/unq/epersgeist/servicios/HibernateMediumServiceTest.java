package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HibernateMediumServiceTest {
    private final HibernateMediumDAO mediumDao = new HibernateMediumDAO();
    private final MediumServiceImpl serviceMedium = new MediumServiceImpl(mediumDao);
    private Medium epifanio;

    @BeforeEach
    public void setUp() {
        epifanio = new Medium("Epifanio el poderoso", 150, 50);
    }

    @Test
    void test() {
    }

    @AfterEach
    void tearDown() {
        HibernateTransactionRunner.runTrx(() -> {
            mediumDao.eliminarTodos();
            return null;
        });
    }
}
