package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.controller.dtos.mediums.request.MediumCreateDTO;
import ar.edu.unq.epersgeist.controller.dtos.mediums.request.MediumUpdateDTO;
import ar.edu.unq.epersgeist.controller.dtos.mediums.response.MediumResponseDTO;
import ar.edu.unq.epersgeist.controller.mappers.MapperEspiritu;
import ar.edu.unq.epersgeist.controller.mappers.MapperMedium;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mediums")
@AllArgsConstructor
public class MediumControllerREST {
    private final MediumService mediumService;
    private final UbicacionService ubicacionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MediumResponseDTO> obtenerTodosLosMediums() {
        return mediumService.recuperarTodos().stream()
                .map(MapperMedium::toDTO).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MediumResponseDTO obtenerMediumPorId(@PathVariable Long id) {
        Medium medium = mediumService.recuperar(id);
        return MapperMedium.toDTO(medium);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MediumResponseDTO crearMedium(@Valid @RequestBody MediumCreateDTO dtoRequest) {
        Long idUbicacion = dtoRequest.ubicacionId();
        Ubicacion ubicacion = ubicacionService.recuperar(idUbicacion);

        Medium medium = mediumService.crear(MapperMedium.toModelo(dtoRequest, ubicacion));
        return MapperMedium.toDTO(medium);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void actualizarMedium(@PathVariable Long id, @Valid @RequestBody MediumUpdateDTO dtoRequest) {
        Medium medium = mediumService.recuperar(id);

        MapperMedium.actualizar(medium, dtoRequest);

        mediumService.actualizar(medium);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void deleteMedium(@PathVariable Long id) {
        mediumService.eliminar(id);
    }

    @PutMapping("/{id}/descansar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void descansar(@PathVariable Long id) {
        mediumService.descansar(id);
    }

    @PutMapping("/{idM}/invocar/espiritus/{idE}")
    @ResponseStatus(HttpStatus.OK)
    public EspirituResponseDTO invocar(@PathVariable Long idM, @PathVariable Long idE) {
        Espiritu espiritu = mediumService.invocar(idM, idE);
        return MapperEspiritu.toDTO(espiritu);
    }

    @PutMapping("/{idM1}/exorcizar/mediums/{idM2}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void exorcizar(@PathVariable Long idM1, @PathVariable Long idM2) {
        mediumService.exorcizar(idM1, idM2);
    }

    @GetMapping("/{id}/espiritus")
    @ResponseStatus(HttpStatus.OK)
    public List<EspirituResponseDTO> espiritus(@PathVariable Long id) {
        List<Espiritu> espiritus = mediumService.espiritus(id);
        return espiritus.stream().
                map(MapperEspiritu::toDTO).
                collect(Collectors.toList());
    }

    @PutMapping("/{idM}/mover/{idU}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void mover(@PathVariable Long idM, @PathVariable Long idU) {
        mediumService.mover(idM, idU);
    }

}
