package ar.edu.unq.epersgeist;

import org.junit.jupiter.api.BeforeAll;

import static java.util.logging.LogManager.getLogManager;

public class JDBCEspirituServiceTest {

    @BeforeAll
    static void setupLogging() {
        try {
            // Cargamos la configuración de logging
            getLogManager().readConfiguration(
                    JDBCPersonajeServiceTest.class.getClassLoader().getResourceAsStream("logging.properties")
            );
        } catch (Exception e) {
            System.err.println("No se pudo cargar la configuración de logging: " + e.getMessage());
        }
    }
}
