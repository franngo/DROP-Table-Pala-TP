package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;

public class HibernateTest {
    protected final void runTrx(Runnable block) {
        HibernateTransactionRunner.runTrx(() -> {
            block.run();
            return null;
        });
    }
}
