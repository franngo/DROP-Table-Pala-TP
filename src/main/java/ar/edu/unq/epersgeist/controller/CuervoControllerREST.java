package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.persistencia.raven.cuervo.Cuervo;
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

    // ------------------------------------------------------------------------

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Cuervo> obtenerTodos() {
        return cuervoService.obtenerTodos();
    }

    // ------------------------------------------------------------------------

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Cuervo obtenerPorId(@PathVariable String id) {
        return cuervoService.obtenerPorId(id);
    }

    // ------------------------------------------------------------------------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cuervo crear(@RequestBody String nombre) {
        return cuervoService.crear(nombre);
    }

    // ------------------------------------------------------------------------

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Cuervo actualizar(
            @PathVariable String id,
            @RequestBody Cuervo cuervo
    ) {
        cuervo.setId(id);
        return cuervoService.actualizar(cuervo);
    }

    // ------------------------------------------------------------------------

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        cuervoService.eliminar(id);
    }

    // ------------------------------------------------------------------------

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarTodos() {
        cuervoService.eliminarTodos();
    }
}
