package ar.edu.unq.epersgeist.persistencia;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateUbicacionDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HibernateUbicacionDAOTest {
    private UbicacionDAO ubicacionDAO;

    @BeforeEach
    public void setUp() {
        ubicacionDAO = new HibernateUbicacionDAO();
    }

    // ============================== GUARDAR ==============================

    @Test
    public void debeGuardarUbicacion() {
        this.runTrx(() -> {
            Ubicacion ubicacion = new Ubicacion("Santuario de la Luz");
            Ubicacion ubicacionGuardada = ubicacionDAO.guardar(ubicacion);

            assertNotNull(ubicacionGuardada.getId());
        });
    }

    // ============================== HELPERS ==============================

    private void runTrx(Runnable block) {
        HibernateTransactionRunner.runTrx(() -> {
            block.run();
            return null;
        });
    }

    // ============================== TEARDOWN ==============================

    @AfterEach
    void tearDown() {
        // TODO
    }
}
