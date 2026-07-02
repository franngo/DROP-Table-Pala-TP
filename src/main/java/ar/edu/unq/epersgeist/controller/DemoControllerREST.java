package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.controller.mappers.MapperEspiritu;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.servicios.DemoService;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
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

    @GetMapping("/amos")
    @ResponseStatus(HttpStatus.OK)
    public List<EspirituResponseDTO> amos() throws InterruptedException {
        List<Espiritu> amos = demoService.historialDeAmos();
        return amos.stream().map(MapperEspiritu::toDTO).toList();
    }

    // --------------------------------------------------------------------------------
    // Demo 2: El mapa de calor temporal
    // --------------------------------------------------------------------------------

    @GetMapping("/mapa-calor")
    @ResponseStatus(HttpStatus.OK)
    public List<GeoJsonPoint> ejecutarDemoMapaCalor() throws Exception {
        return demoService.demoMapaDeCalorTemporal();
    }

    // --------------------------------------------------------------------------------
    // Demo 3: Análisis de daño de batalla
    // --------------------------------------------------------------------------------

    @GetMapping("/danio-batalla")
    @ResponseStatus(HttpStatus.OK)
    public void ejecutarDemoDanioBatalla() {
        demoService.demoCostoMoverseUltimaUbicacion();
    }

    // --------------------------------------------------------------------------------

    @DeleteMapping("/borrar-todo")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAll() {
        demoService.deleteAll();
    }

    // --------------------------------------------------------------------------------
}
