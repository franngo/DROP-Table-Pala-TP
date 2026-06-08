package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.NoExisteSantuarioCorruptoException;
import ar.edu.unq.epersgeist.modelo.reportes.ReporteSantuarioMasCorrupto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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

    private List<Coordenada> coordenadas;


    @Nested
    class CasosExitosos {
        @BeforeEach
        void setUp() {

            coordenadas = List.of(
                    new Coordenada(-34.81, -58.22),
                    new Coordenada(-34.82, -58.24),
                    new Coordenada(-34.80, -58.20)
            );
            //ubicaciones
            berazategui = ubicacionService.crear(new Santuario("Berazategui", 100),coordenadas);
            varela = ubicacionService.crear(new Santuario("Varela", 100),coordenadas);

            //demonios
            matias = espirituService.crear(new Demonio("Matias", berazategui), new Coordenada(-34.81, -58.22));
            juanchi = espirituService.crear(new Demonio("juanchi", varela), new Coordenada(-34.82, -58.23));
            franco = espirituService.crear(new Demonio("franco", varela), new Coordenada(-34.83, -58.24));
            yoel = espirituService.crear(new Demonio("yoel", varela), new Coordenada(-34.84, -58.25));
            nico = espirituService.crear(new Demonio("nico", varela), new Coordenada(-34.85, -58.26));


            //angeles
            chiqui = espirituService.crear(new Angelical("chiqui", berazategui), new Coordenada(-34.86, -58.27));
            leonardo = espirituService.crear(new Angelical("leonardo", varela), new Coordenada(-34.87, -58.28));

            valentin = mediumService.crear(new Medium("Valentin", 100, 100, varela), new Coordenada(-34.87, -58.29));
            pancho = mediumService.crear(new Medium("Pancho", 100, 100, varela), new Coordenada(-34.88, -58.30));

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
        void empateEntreMediumsDevuelveElQueTieneElIdMenor() {


            espirituService.conectar(nico.getId(), pancho.getId());
            espirituService.conectar(franco.getId(), pancho.getId());


            ReporteSantuarioMasCorrupto reporte =
                    estadisticaService.santuarioCorrupto();

            assertEquals("Valentin", reporte.getNombreMedium());

        }

        @Test
        void reporteSinMediumNoRompe() {

            Espiritu juanchiAct = espirituService.recuperar(juanchi.getId());
            Espiritu yoelAct = espirituService.recuperar(yoel.getId());

            juanchiAct.setMediumCon(null);
            yoelAct.setMediumCon(null);
            espirituService.actualizar(juanchiAct);
            espirituService.actualizar(yoelAct);

            ReporteSantuarioMasCorrupto reporte =
                    estadisticaService.santuarioCorrupto();

            assertEquals("Varela", reporte.getNombreSantuario());
            assertNull(reporte.getNombreMedium());
        }

        @Test
        void empateEntreSantuariosDevuelveCualquiera() {
            //llegan 3 demonios mas a berazategui y ahora podria darme cualquier
            // santuario el reporte
            espirituService.crear(new Demonio("benja", berazategui), new Coordenada(-34.89, -58.31));
            espirituService.crear(new Demonio("fidel", berazategui), new Coordenada(-34.90, -58.32));
            espirituService.crear(new Demonio("raul", berazategui), new Coordenada(-34.91, -58.33));

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
