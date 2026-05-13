package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Cementerio;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UbicacionTest {
    // --------------------------------------------------------------------------------
    // CONSTANTES
    // --------------------------------------------------------------------------------

    private static final String NOMBRE_SANTUARIO = "Santuario de Lumen Silente";
    private static final String NOMBRE_CEMENTERIO = "Cementerio de Nox Umbra";

    private static final Integer ENERGIA_MINIMA = 0;
    private static final Integer ENERGIA_MAXIMA = 100;
    private static final Integer ENERGIA_VALIDA = 50;

    private static final Integer ENERGIA_MENOR_AL_MINIMO = -1;
    private static final Integer ENERGIA_MAYOR_AL_MAXIMO = 101;

    // --------------------------------------------------------------------------------
    // ATRIBUTOS
    // --------------------------------------------------------------------------------

    private Ubicacion santuario;
    private Ubicacion cementerio;

    private Validator validator;

    // --------------------------------------------------------------------------------
    // SETUP
    // --------------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        santuario = new Santuario(NOMBRE_SANTUARIO, ENERGIA_VALIDA);
        cementerio = new Cementerio(NOMBRE_CEMENTERIO, ENERGIA_VALIDA);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // --------------------------------------------------------------------------------
    // HELPERS
    // --------------------------------------------------------------------------------

    private <T> Set<ConstraintViolation<T>> validar(T obj) {
        return validator.validate(obj);
    }

    // --------------------------------------------------------------------------------
    // CREACION DE UBICACIONES
    // --------------------------------------------------------------------------------

    @Nested
    class CreacionDeUbicaciones {
        @Test
        void unaUbicacionSeCreaConNombreCorrecto() {
            assertEquals(NOMBRE_SANTUARIO, santuario.getNombre());
        }

        @Test
        void unaUbicacionSeCreaConEnergiaCorrecta() {
            assertEquals(ENERGIA_VALIDA, santuario.getEnergia());
        }
    }

    // --------------------------------------------------------------------------------
    // LIMITES DE ENERGIA
    // --------------------------------------------------------------------------------

    @Nested
    class LimitesDeEnergia {
        @Test
        void energiaMinimaEsValida() {
            Ubicacion ubicacion = new Santuario(NOMBRE_SANTUARIO, ENERGIA_MINIMA);
            assertTrue(validar(ubicacion).isEmpty());
        }

        @Test
        void energiaMaximaEsValida() {
            Ubicacion ubicacion = new Santuario(NOMBRE_SANTUARIO, ENERGIA_MAXIMA);
            assertTrue(validar(ubicacion).isEmpty());
        }

        @Test
        void energiaNegativaEsInvalida() {
            Ubicacion ubicacion = new Santuario(NOMBRE_SANTUARIO, ENERGIA_MENOR_AL_MINIMO);
            assertFalse(validar(ubicacion).isEmpty());
        }

        @Test
        void energiaMayorA100EsInvalida() {
            Ubicacion ubicacion = new Santuario(NOMBRE_SANTUARIO, ENERGIA_MAYOR_AL_MAXIMO);
            assertFalse(validar(ubicacion).isEmpty());
        }
    }

    // --------------------------------------------------------------------------------
    // EFECTO DE MOVIMIENTO
    // --------------------------------------------------------------------------------


//    @Nested
//    class EfectoDeMovimiento {
//        @Test
//        void santuarioEjecutaSuEfectoSinErrores() {
//            Espiritu espiritu = new Angelical("Miguel", santuario);
//            assertDoesNotThrow(() -> santuario.aplicarEfectoMovimiento(espiritu));
//        }
//
//        @Test
//        void cementerioEjecutaSuEfectoSinErrores() {
//            Espiritu espiritu = new Demonio("Belcebu", cementerio);
//            assertDoesNotThrow(() -> cementerio.aplicarEfectoMovimiento(espiritu));
//        }
//    }
}
