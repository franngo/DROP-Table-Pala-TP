package ar.edu.unq.epersgeist.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UbicacionTest {
    private Ubicacion ubicacion;

    @BeforeEach
    void setUp() {
        this.ubicacion = new Ubicacion("Santuario de la Luz");
    }

    @Test
    public void debeCrearUbicacionConElNombreIndicado() {
        assertEquals("Santuario de la Luz", this.ubicacion.getNombre());
    }

    @Test
    public void debeCrearUbicacionConIdNulo() {
        assertNull(this.ubicacion.getId());
    }
}
