package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.persistencia.raven.Cuervo;
import ar.edu.unq.epersgeist.servicios.CuervoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuervos")
@AllArgsConstructor
public class CuervoControllerREST {
    private final CuervoService cuervoService;

    // --------------------------------------------------------------------------------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cuervo crear(@RequestBody CuervoRequest request) {
        return cuervoService.crear(request.nombre());
    }

    // --------------------------------------------------------------------------------

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Cuervo recuperar(@PathVariable Long id) {
        return cuervoService.recuperar(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Cuervo> recuperarTodos() {
        return cuervoService.recuperarTodos();
    }

    // --------------------------------------------------------------------------------

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void actualizar(@RequestBody Cuervo cuervo) {
        cuervoService.actualizar(cuervo);
    }

    // --------------------------------------------------------------------------------

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        cuervoService.eliminar(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarTodos() {
        cuervoService.eliminarTodos();
    }

    // --------------------------------------------------------------------------------

    @GetMapping("/{id}/historial")
    @ResponseStatus(HttpStatus.OK)
    public List<Cuervo> recuperarHistorial(@PathVariable Long id) {
        return cuervoService.recuperarHistorial(id);
    }

    // --------------------------------------------------------------------------------

    public record CuervoRequest(String nombre) {
    }
}
