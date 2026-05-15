package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.NoExisteSantuarioCorruptoException;
import ar.edu.unq.epersgeist.modelo.reportes.ReporteSantuarioMasCorrupto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EstadisticaServiceTest {
    private Espiritu matias;
    private Espiritu juanchi;
    private Espiritu franco;
    private Espiritu yoel;

    private Espiritu chiqui;
    private Espiritu leonardo;
    private Espiritu nico;

    private Ubicacion berazategui;
    private Ubicacion varela;

    private Medium valentin;
    private Medium pancho;
    @Autowired
    private EstadisticaService estadisticaService;
    @Autowired
    private UbicacionService ubicacionService;
    @Autowired
    private EspirituService espirituService;
    @Autowired
    private MediumService mediumService;

    @Nested
    class CasosExitosos {
        @BeforeEach
        void setUp() {
            //ubicaciones
            berazategui = ubicacionService.crear(new Santuario("Berazategui", 100));
            varela = ubicacionService.crear(new Santuario("Varela", 100));

            //demonios
            matias = espirituService.crear(new Demonio("Matias", berazategui));
            juanchi = espirituService.crear(new Demonio("juanchi", varela));
            franco = espirituService.crear(new Demonio("franco", varela));
            yoel = espirituService.crear(new Demonio("yoel", varela));
            nico = espirituService.crear(new Demonio("nico", varela));


            //angeles
            chiqui = espirituService.crear(new Angelical("chiqui", berazategui));
            leonardo = espirituService.crear(new Angelical("leonardo", varela));

            valentin = mediumService.crear(new Medium("Valentin", 100, 100, varela));
            pancho = mediumService.crear(new Medium("Pancho", 100, 100, varela));

            espirituService.conectar(juanchi.getId(), valentin.getId());
            espirituService.conectar(yoel.getId(), valentin.getId());

        }


        @Test
        void elReporteSeConsigueExitosamente() {


            ReporteSantuarioMasCorrupto reporte =
                    estadisticaService.santuarioCorrupto();

            assertEquals(
                    "Varela",
                    reporte.getNombreSantuario()
            );

            assertEquals(
                    "Valentin",
                    reporte.getNombreMedium()
            );

            assertEquals(
                    4L,
                    reporte.getCantidadTotalDemonios()
            );

            assertEquals(
                    2L,
                    reporte.getDemoniosLibres()
            );
        }

        @Test
        void empateEntreMediumsDevuelveCualquiera() {


            espirituService.conectar(nico.getId(), pancho.getId());
            espirituService.conectar(franco.getId(), pancho.getId());


            ReporteSantuarioMasCorrupto reporte =
                    estadisticaService.santuarioCorrupto();

            assertThat(reporte.getNombreMedium()).isIn("Valentin", "Pedro");

        }

        @Transactional
        @Test
        void reporteSinMediumNoRompe() {

            Espiritu juanchiAct = espirituService.recuperar(juanchi.getId());
            Espiritu yoelAct = espirituService.recuperar(yoel.getId());

            juanchiAct.desvincularse();
            yoelAct.desvincularse();
            espirituService.actualizar(juanchiAct);
            espirituService.actualizar(yoelAct);

            ReporteSantuarioMasCorrupto reporte =
                    estadisticaService.santuarioCorrupto();

            assertEquals(
                    "Varela",
                    reporte.getNombreSantuario()
            );

            assertNull(
                    reporte.getNombreMedium()
            );
        }

        @Test
        void empateEntreSantuariosDevuelveCualquiera() {
            //llegan 3 demonios mas a berazategui y ahora podria darme cualquier
            // santuario el reporte
            espirituService.crear(new Demonio("benja", berazategui));
            espirituService.crear(new Demonio("fidel", berazategui));
            espirituService.crear(new Demonio("raul", berazategui));

            ReporteSantuarioMasCorrupto reporte =
                    estadisticaService.santuarioCorrupto();
            assertThat(reporte.getNombreSantuario()).isIn("Varela", "Berazategui");

        }


        @Test
        void todosLosDemoniosLigados() {

            espirituService.conectar(franco.getId(), valentin.getId());
            espirituService.conectar(nico.getId(), valentin.getId());


            ReporteSantuarioMasCorrupto reporte =
                    estadisticaService.santuarioCorrupto();

            assertEquals(
                    0L,
                    reporte.getDemoniosLibres()
            );
        }

    }

    @Nested
    class CasosExcepciones {
        @Test
        void lanzaExcepcionSiNoHaySantuarios() {

            assertThrows(
                    NoExisteSantuarioCorruptoException.class,
                    () -> estadisticaService.santuarioCorrupto()
            );
        }

    }
    
    @AfterEach
    void tearDown() {
        espirituService.eliminarTodos();
        mediumService.eliminarTodos();
        ubicacionService.eliminarTodos();

    }

}
