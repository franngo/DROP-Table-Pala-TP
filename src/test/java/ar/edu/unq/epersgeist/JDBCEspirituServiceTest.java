package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.persistencia.dao.jdbc.JDBCEspirituDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import org.junit.jupiter.api.*;

import static java.util.logging.LogManager.getLogManager;
import static org.junit.jupiter.api.Assertions.*;

public class JDBCEspirituServiceTest {
    private final JDBCEspirituDAO espirituDao = new JDBCEspirituDAO();
    private final EspirituService serviceEspiritu = new EspirituServiceImpl(espirituDao);
    private Espiritu matias;


    @BeforeAll
    static void setupLogging() {
        try {
            // Cargamos la configuración de logging
            getLogManager().readConfiguration(
                    JDBCEspirituServiceTest.class.getClassLoader().getResourceAsStream("logging.properties")
            );
        } catch (Exception e) {
            System.err.println("No se pudo cargar la configuración de logging: " + e.getMessage());
        }
    }

    @BeforeEach
    public void setUp() {

        matias = new Espiritu("Pokemon", "Matias");
    }

    @Test
    public void seUsaElServicioParaGuardarModelo() {

        Espiritu espirituP = serviceEspiritu.crear(matias);
        var otroEspiritu = serviceEspiritu.recuperar(espirituP.getId());

        assertEquals(espirituP.getTipo(), otroEspiritu.getTipo());
        assertEquals(espirituP.getNivelDeConexion(), otroEspiritu.getNivelDeConexion());
        assertEquals(espirituP.getNombre(), otroEspiritu.getNombre());
        assertEquals(espirituP.getId(), otroEspiritu.getId());

        //Pero no son el mismo objeto =(
        //A esto nos referimos con "perdida de identidad"
        Assertions.assertNotEquals(espirituP, otroEspiritu);
    }

    @Test
    void eliminoUnEspirituCorrectamente() {

        Espiritu espirituEliminar = serviceEspiritu.crear(matias);

        assertNotNull(espirituEliminar.getId());


        serviceEspiritu.eliminar(espirituEliminar.getId());


        assertThrows(RuntimeException.class, () -> {
            serviceEspiritu.recuperar(espirituEliminar.getId());
        });
    }

  /*
    @Test
    public void seConectaUnEspirituConUnMedium() {

        // Arrange
        // Espiritu espirituM = new Espiritu("Pokemon", "Matias");
        Espiritu espirituP = espirituDao.crear(matias);
        Medium mediumM = new Medium("Juan", 120, 10);

        System.out.println("espirituP");
        System.out.println(espirituP.hashCode());

        assertEquals(0, espirituP.getNivelDeConexion());
        // Act
        Medium medium2 = serviceEspiritu.conectar(espirituP.getId(), mediumM);

        assertFalse(medium2.getEspiritus().isEmpty());


        // Assert
        Espiritu espirituActualizado = serviceEspiritu.recuperar(espirituP.getId());
        //System.out.println("espirituActualizado");
        // System.out.println(espirituActualizado.hashCode());

        assertEquals(10, espirituActualizado.getNivelDeConexion());


    }*/

    @AfterEach
    void tearDown() {
        // TODO implementar eliminar
        espirituDao.eliminarTodos();
    }

}
