package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.controller.mappers.MapperEspiritu;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.servicios.DemoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoControllerREST {

    private final DemoService demoService;

    public DemoControllerREST(DemoService demoService) {
        this.demoService = demoService;
    }

    // --------------------------------------------------------------------------------
    // Demo 1: Historial de Amos
    // --------------------------------------------------------------------------------

    @PostMapping("/amos")
    @ResponseStatus(HttpStatus.OK)
    public List<EspirituResponseDTO> amos() {
        List<Espiritu> amos = demoService.historialDeAmos();
        return amos.stream().map(MapperEspiritu::toDTO).toList();
    }

    // --------------------------------------------------------------------------------
    // Demo 2: El mapa de calor temporal
    // --------------------------------------------------------------------------------

//    @PostMapping("/mapa-calor")
//    @ResponseStatus(HttpStatus.OK)
//    public String ejecutarDemoMapaCalor() throws Exception {
//        demoService.demoMapaDeCalorTemporal();
//        return "Demo 2 (Mapa de Calor Temporal) ejecutada con éxito. Revisá los logs de la consola.";
//    }

    // --------------------------------------------------------------------------------
    // Demo 3: Análisis de daño de batalla
    // --------------------------------------------------------------------------------

//    @PostMapping("/danio-batalla")
//    @ResponseStatus(HttpStatus.OK)
//    public String ejecutarDemoDanioBatalla() throws Exception {
//        demoService.demoDanioDeBatalla();
//        return "Demo 3 (Análisis de Daño de Batalla) ejecutada con éxito. Revisá los logs de la consola.";
//    }

    // --------------------------------------------------------------------------------

    @DeleteMapping("/borrar-todo")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAll() {
        demoService.deleteAll();
    }

    // --------------------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String manejarErrores(Exception e) {
        return "Error al ejecutar la demo: " + e.getMessage();
    }
}
