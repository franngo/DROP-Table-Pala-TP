package ar.edu.unq.epersgeist.persistencia;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.jdbc.JDBCEspirituDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JDBCEspirituDAOTest {
    private EspirituDAO espirituDao = new JDBCEspirituDAO();
    private Espiritu espirituM;

    @BeforeEach
    public void setUp() {
        espirituM = new Espiritu("Pokemon", "das");
    }

    @Test
    public void seCreaElEspirituEnLaBase() {


        //veo que el modelo no tiene id
        assertNull(espirituM.getId());
        //exercise
        Espiritu espirituP = espirituDao.crear(espirituM);

        //Compruebo que no son el mismo
        assertNotEquals(espirituM, espirituP);

        //Compruebo que el modelo se persistio porque tiene id
        assertNotNull(espirituP.getId());


    }


    @Test
    public void seRecuperaUnEspirituDadoUnId() {

        Espiritu unEspiritu = espirituDao.crear(espirituM);
        assertNotNull(unEspiritu.getId());
        System.out.println(unEspiritu);

        Espiritu espirituRecuperado = espirituDao.recuperar(unEspiritu.getId());
        System.out.println(espirituRecuperado);

        assertNotNull(espirituRecuperado.getId());
    }

    @Test
    public void seEliminaUnEspirituExistente() {

        // Arrange
        Espiritu creado = espirituDao.crear(espirituM);
        assertNotNull(creado.getId());

        // Act
        espirituDao.eliminar(creado.getId());

        // Assert
        assertThrows(RuntimeException.class, () -> {
            espirituDao.recuperar(creado.getId());
        });
    }

    @Test
    public void noFallaAlEliminarUnEspirituInexistente() {

        // Arrange
        Long idInexistente = 99999L;

        // Act & Assert
        assertThrows(RuntimeException.class,() -> {
            espirituDao.eliminar(idInexistente);
        });
    }

    @Test
    public void recuperarTodosDevuelveListaVacia() {

        List<Espiritu> lista = espirituDao.recuperarTodos();

        assertNotNull(lista);
        assertTrue(lista.isEmpty());
    }

    @Test
    public void recuperarTodosDevuelveUnEspiritu() {

        espirituDao.crear(new Espiritu("Pokemon", "Matias"));

        List<Espiritu> lista = espirituDao.recuperarTodos();

        assertEquals(1, lista.size());
    }

    @Test
    public void recuperarTodosDevuelveVariosElementos() {

        espirituDao.crear(new Espiritu("Pokemon", "Matias"));
        espirituDao.crear(new Espiritu("Pokemon", "Juan"));

        List<Espiritu> lista = espirituDao.recuperarTodos();

        assertEquals(2, lista.size());
    }

    @Test
    public void recuperarTodosDevuelveListaOrdenadaPorNombre() {

        espirituDao.crear(new Espiritu("Pokemon", "Matias"));
        espirituDao.crear(new Espiritu("Pokemon", "Juanchi el Pastelero"));
        espirituDao.crear(new Espiritu("Pokemon", "Alberto"));

        List<Espiritu> lista = espirituDao.recuperarTodos();

        assertEquals("Alberto", lista.get(0).getNombre());
        assertEquals("Juanchi el Pastelero", lista.get(1).getNombre());
        assertEquals("Matias", lista.get(2).getNombre());
    }

    @Test
    public void seActualizaUnEspirituExistente() {

        // Arrange
        Espiritu creado = espirituDao.crear(new Espiritu("Pokemon", "Matias"));
        assertNotNull(creado.getId());

        // Modifico datos
        creado.setNombre("NombreActualizado");

        // Act
        espirituDao.actualizar(creado);

        // Assert
        Espiritu actualizado = espirituDao.recuperar(creado.getId());

        assertEquals("NombreActualizado", actualizado.getNombre());
    }

    @Test
    public void fallaAlActualizarUnEspirituInexistente() {

        // Arrange
        Espiritu inexistente = new Espiritu("Pokemon", "NoExisto");
        inexistente.setId(12312L); // ID que no está en la base

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            espirituDao.actualizar(inexistente);
        });
    }

    @Test
    public void fallaAlActualizarUnEspirituSinId() {

        // Arrange
        Espiritu espirituMsinId = new Espiritu("Pokemon", "NoEstoyPersistido");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            espirituDao.actualizar(espirituMsinId);
        });
    }

    @AfterEach
    void tearDown() {
        // TODO implementar eliminar
        espirituDao.eliminarTodos();
    }
}
