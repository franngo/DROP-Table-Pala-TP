package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.reportes.ReporteSantuarioMasCorrupto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertTrue(
                reporte.getNombreMedium().equals("Valentin")
                        ||
                        reporte.getNombreMedium().equals("Pedro")
        );
    }
    //    @Test
//    void lanzaExcepcionSiNoHaySantuarios() {
//
//        //limpio la bbdd
//        //espirituService.eliminarTodos();
//        //mediumService.eliminarTodos();
//        // ubicacionService.eliminarTodos();
//
//        assertThrows(
//                NoExisteSantuarioCorruptoException.class,
//                () -> estadisticaService.santuarioCorrupto()
//        );
//    }
//
//    @Test
//    void reporteSinMediumNoRompe() {
//
//
//        Ubicacion quilmes = ubicacionService.crear(
//                new Santuario("Quilmes", 100)
//        );
//
//        espirituService.crear(
//                new Demonio("", quilmes)
//        );
//
//        ReporteSantuarioMasCorrupto reporte =
//                estadisticaService.santuarioCorrupto();
//
//        assertEquals(
//                "Quilmes",
//                reporte.getNombreSantuario()
//        );
//
//        assertNull(
//                reporte.getNombreMedium()
//        );
//    }
//
//    @Test
//    void todosLosDemoniosLigados() {
//
//        Ubicacion varela =
//                ubicacionService.crear(
//                        new Santuario("Varela", 100)
//                );
//
//        Espiritu d1 =
//                espirituService.crear(
//                        new Demonio("D1", varela)
//                );
//
//        Espiritu d2 =
//                espirituService.crear(
//                        new Demonio("D2", varela)
//                );
//
//        Medium valentin =
//                mediumService.crear(
//                        new Medium("Valentin", 100, 100, varela)
//                );
//
//        espirituService.conectar(
//                d1.getId(),
//                valentin.getId()
//        );
//
//        espirituService.conectar(
//                d2.getId(),
//                valentin.getId()
//        );
//
//        ReporteSantuarioMasCorrupto reporte =
//                estadisticaService.santuarioCorrupto();
//
//        assertEquals(
//                0L,
//                reporte.getDemoniosLibres()
//        );
//    }
//
//

//    @Test
//    void empateEntreSantuariosDevuelveCualquiera() {
//
//        Ubicacion varela =
//                ubicacionService.crear(
//                        new Santuario("Varela", 100)
//                );
//
//        Ubicacion quilmes =
//                ubicacionService.crear(
//                        new Santuario("Quilmes", 100)
//                );
//
//        espirituService.crear(
//                new Demonio("D1", varela)
//        );
//
//        espirituService.crear(
//                new Angelical("A1", varela)
//        );
//
//        espirituService.crear(
//                new Demonio("D2", quilmes)
//        );
//
//        espirituService.crear(
//                new Angelical("A2", quilmes)
//        );
//
//        ReporteSantuarioMasCorrupto reporte =
//                estadisticaService.santuarioCorrupto();
//
//        assertTrue(
//                reporte.getNombreSantuario().equals("Varela")
//                        ||
//                        reporte.getNombreSantuario().equals("Quilmes")
//        );
//    }

    @AfterEach
    void tearDown() {
        espirituService.eliminarTodos();
        mediumService.eliminarTodos();
        ubicacionService.eliminarTodos();

    }

}
