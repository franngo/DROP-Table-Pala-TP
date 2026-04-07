package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.jdbc.JDBCEspirituDAO;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JDBCEspirituServiceTest {
    private final JDBCEspirituDAO espirituDao = new JDBCEspirituDAO();
    private final EspirituService serviceEspiritu = new EspirituServiceImpl(espirituDao);
    private Espiritu matias;

     /*
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
    */

    @BeforeEach
    public void setUp() {

        matias = new Espiritu("Pokemon", "Matias");
    }
    @Test
    void deberiaAsignarIdAlCrearUnEspiritu() {
        Espiritu espiritu = serviceEspiritu.crear(matias);
        assertNotNull(espiritu.getId());
    }

    @Test
    void deberiaRecuperarCorrectamenteLosDatosDelEspiritu(){
        Espiritu espirituP = serviceEspiritu.crear(matias);
        Espiritu espirituRecuperado = serviceEspiritu.recuperar(espirituP.getId());

        assertEquals(espirituP.getId(),espirituRecuperado.getId());
        assertEquals(espirituP.getTipo(),espirituRecuperado.getTipo());
        assertEquals(espirituP.getNombre(),espirituRecuperado.getNombre());
        assertEquals(espirituP.getNivelDeConexion(),espirituRecuperado.getNivelDeConexion());
    }

    @Test
    void deberiaActualizarSoloLosCamposModificados(){
        Espiritu espirituP = serviceEspiritu.crear(matias);
        espirituP.setTipo("Otro Pokemon");
        espirituP.setNivelDeConexion(99);
        espirituP.setNombre("Juanchi el pastelero");
        serviceEspiritu.actualizar(espirituP);
        Espiritu espirituActualizado = espirituDao.recuperar(espirituP.getId());

        assertEquals(espirituP.getTipo(),espirituActualizado.getTipo());
        assertEquals(espirituP.getNombre(),espirituActualizado.getNombre());
        assertEquals(espirituP.getNivelDeConexion(),espirituActualizado.getNivelDeConexion());
    }

    @Test
   void noDeberiaActualizarConIdNulo() {

        assertThrows(RuntimeException.class, () -> {
            serviceEspiritu.actualizar(matias);
        });
    }
    @Test
    void deberiaEliminarYNoPoderRecuperarloLuego(){
        Espiritu espirituP = serviceEspiritu.crear(matias);

        serviceEspiritu.eliminar(espirituP.getId());
        assertThrows(RuntimeException.class, () -> {
            serviceEspiritu.recuperar(espirituP.getId());
        });    }

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


    @Test
    public void seConectaUnEspirituConUnMedium() {

        Espiritu espirituP = espirituDao.crear(matias);
        Medium mediumM = new Medium("Juan", 120, 10);

        assertEquals(0, espirituP.getNivelDeConexion());
        // Act
        Medium medium2 = serviceEspiritu.conectar(espirituP.getId(), mediumM);

        assertFalse(medium2.getEspiritus().isEmpty());

        Espiritu espirituActualizado = serviceEspiritu.recuperar(espirituP.getId());

        assertEquals(10, espirituActualizado.getNivelDeConexion());
    }

    @Test
    void noDeberiaConectarUnEspirituInexistenteConUnMedium() {
        Medium medium = new Medium("Juan", 120, 10);

        assertThrows(RuntimeException.class, () -> {
            serviceEspiritu.conectar(9999L, medium);
        });
    }

    @Test
    void noDeberiaConectarUnEspirituConUnMediumNulo() {
        Espiritu espiritu = serviceEspiritu.crear(matias);

        assertThrows(RuntimeException.class, () -> {
            serviceEspiritu.conectar(espiritu.getId(), null);
        });
    }

    @Test
    void noDeberiaConectarConIdNulo() {
        Medium medium = new Medium("Juan", 120, 10);

        assertThrows(RuntimeException.class, () -> {
            serviceEspiritu.conectar(null, medium);
        });
    }

    @Test
    void deberiaAsociarElEspirituAlMedium() {

        Espiritu espirituP = serviceEspiritu.crear(matias);
        Medium mediumJuan = new Medium("Juan", 120, 10);

        Medium mediumConectado = serviceEspiritu.conectar(espirituP.getId(), mediumJuan);

        assertEquals(1, mediumJuan.getEspiritus().size());

    }

    @Test
    void deberiaPersistirElNivelDeConexionLuegoDeConectar() {
        Espiritu espiritu = serviceEspiritu.crear(matias);
        Medium medium = new Medium("Juan", 120, 25);

        serviceEspiritu.conectar(espiritu.getId(), medium);

        Espiritu recuperado = serviceEspiritu.recuperar(espiritu.getId());

        assertEquals(medium.getCantFortalece(), recuperado.getNivelDeConexion());
    }

    @Test
    void deberiaPermitirConectarVariosEspiritusAlMismoMedium() {
        Espiritu espiritu1 = serviceEspiritu.crear(new Espiritu("Pokemon", "Matias"));
        Espiritu espiritu2 = serviceEspiritu.crear(new Espiritu("Pokemon", "Juanchi el Pastelero"));

        Medium medium = new Medium("Juan", 120, 10);

        serviceEspiritu.conectar(espiritu1.getId(), medium);
        Medium mediumActualizado = serviceEspiritu.conectar(espiritu2.getId(), medium);

        assertEquals(2, mediumActualizado.getEspiritus().size());
    }


    @Test
    void noDeberiaConectarUnEspirituEliminado() {
        Espiritu espiritu = serviceEspiritu.crear(matias);
        serviceEspiritu.eliminar(espiritu.getId());

        Medium medium = new Medium("Juan", 120, 10);

        assertThrows(RuntimeException.class, () -> {
            serviceEspiritu.conectar(espiritu.getId(), medium);
        });
    }

    @Test
    void deberiaMantenerLosDatosOriginalesDelEspirituLuegoDeConectar() {
        Espiritu espiritu = serviceEspiritu.crear(matias);

        String tipoOriginal = espiritu.getTipo();
        String nombreOriginal = espiritu.getNombre();

        Medium medium = new Medium("Juan", 120, 10);

        serviceEspiritu.conectar(espiritu.getId(), medium);

        Espiritu recuperado = serviceEspiritu.recuperar(espiritu.getId());

        assertEquals(tipoOriginal, recuperado.getTipo());
        assertEquals(nombreOriginal, recuperado.getNombre());
    }

    @AfterEach
    void tearDown() {
        // limpiar BBDD
        espirituDao.eliminarTodos();
    }

}
