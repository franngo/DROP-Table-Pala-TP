package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.request.MediumCreateDTO;
import ar.edu.unq.epersgeist.controller.dtos.espiritus.request.MediumUpdateDTO;
import ar.edu.unq.epersgeist.controller.dtos.espiritus.response.MediumResponseDTO;
import ar.edu.unq.epersgeist.controller.mappers.MapperMedium;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.servicios.MediumService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mediums")
@AllArgsConstructor
public class MediumControllerREST {
    private final MediumService mediumService;

    @GetMapping
    public List<MediumResponseDTO> obtenerTodosLosMediums() {
        return mediumService.recuperarTodos().stream().
                map((m) -> MapperMedium.toDetalle(m)).
                collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MediumResponseDTO obtenerMediumPorId(@PathVariable Long id) {
        Medium m = mediumService.recuperar(id);
        return MapperMedium.toDetalle(m);
    }

    @PostMapping
    public void crearMedium(@Valid @RequestBody MediumCreateDTO dtoRequest) {
        Medium m = mediumService.crear(MapperMedium.toModeloCrear(dtoRequest));
        return MapperMedium.toDetalle(m);
    }

    @PutMapping("/{id}")
    public void actualizarMedium(@PathVariable Long id, @Valid @RequestBody MediumUpdateDTO dtoRequest) {
        Medium m = mediumService.recuperar(id);

        MapperMedium.actualizar(m, dtoRequest);

        //mediumService.actualizar(m);
    }

    @DeleteMapping("{id}")
    public void deleteMedium(@PathVariable Long id) {
        mediumService.eliminar(id);
    }

}
