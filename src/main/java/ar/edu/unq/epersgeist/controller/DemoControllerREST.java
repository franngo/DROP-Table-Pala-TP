package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.servicios.DemoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demo")
public class DemoControllerREST {

    private final DemoService demoService;

    public DemoControllerREST(DemoService demoService) {
        this.demoService = demoService;
    }

    // --------------------------------------------------------------------------------
    // Demo 1: La crisis de identidad
    // --------------------------------------------------------------------------------

    @PostMapping("/crisis-identidad")
    @ResponseStatus(HttpStatus.OK)
    public String ejecutarDemoCrisisIdentidad() throws Exception {
        demoService.ejecutarDemosTracking();
        return "Demo 1 (Crisis de Identidad) ejecutada con éxito. Revisá los logs de la consola.";
    }

    // --------------------------------------------------------------------------------
    // Demo 2: El mapa de calor temporal
    // --------------------------------------------------------------------------------

    @PostMapping("/mapa-calor")
    @ResponseStatus(HttpStatus.OK)
    public String ejecutarDemoMapaCalor() throws Exception {
        demoService.demoMapaDeCalorTemporal();
        return "Demo 2 (Mapa de Calor Temporal) ejecutada con éxito. Revisá los logs de la consola.";
    }

    // --------------------------------------------------------------------------------
    // Demo 3: Análisis de daño de batalla
    // --------------------------------------------------------------------------------

    @PostMapping("/danio-batalla")
    @ResponseStatus(HttpStatus.OK)
    public String ejecutarDemoDanioBatalla() throws Exception {
        demoService.demoDanioDeBatalla();
        return "Demo 3 (Análisis de Daño de Batalla) ejecutada con éxito. Revisá los logs de la consola.";
    }

    // --------------------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String manejarErrores(Exception e) {
        return "Error al ejecutar la demo: " + e.getMessage();
    }
}
