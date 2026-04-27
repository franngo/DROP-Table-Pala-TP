package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
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

    @Test
    public void debeCrearUbicacionSinEspiritus() {
        assertEquals(0, this.ubicacion.getEspiritus().size());
    }

    @Test
    public void debeAgregarEspirituALaUbicacion() {
        Espiritu espiritu = new Espiritu("Espíritu de la Luz");
        this.ubicacion.agregarEspiritu(espiritu);

        assertEquals(1, this.ubicacion.getEspiritus().size());
        assertEquals(this.ubicacion, espiritu.getUbicacion());
    }

    @Test
    public void debeAgregarMediumALaUbicacion() {
        Medium medium = new Medium("matias", 10, 10);
        this.ubicacion.agregarMedium(medium);

        assertEquals(1, this.ubicacion.getMediums().size());
        assertEquals(this.ubicacion, medium.getUbicacion());
    }
}
