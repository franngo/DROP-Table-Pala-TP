package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.controller.dtos.mediums.request.MediumCreateDTO;
import ar.edu.unq.epersgeist.controller.dtos.mediums.request.MediumUpdateDTO;
import ar.edu.unq.epersgeist.controller.dtos.mediums.response.MediumResponseDTO;
import ar.edu.unq.epersgeist.controller.mappers.MapperEspiritu;
import ar.edu.unq.epersgeist.controller.mappers.MapperMedium;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
    public List<MediumResponseDTO> obtenerTodosLosMediums() {
        return mediumService.recuperarTodos().stream().
                map((m) -> MapperMedium.toDetalle(m)).
                collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MediumResponseDTO obtenerMediumPorId(@PathVariable Long id) {
        Medium m = mediumService.recuperar(id);
        return MapperMedium.toDTO(m);
    }

    @PostMapping
    public MediumResponseDTO crearMedium(@Valid @RequestBody MediumCreateDTO dtoRequest) {
        Long idU = dtoRequest.ubicacionId();
        Ubicacion u = ubicacionService.recuperar(idU);

        Medium m = mediumService.crear(MapperMedium.toModelo(dtoRequest, u));
        return MapperMedium.toDTO(m);
    }

    @PutMapping("/{id}")
    public void actualizarMedium(@PathVariable Long id, @Valid @RequestBody MediumUpdateDTO dtoRequest) {
        Medium m = mediumService.recuperar(id);

        MapperMedium.actualizar(m, dtoRequest);

        mediumService.actualizar(m);
    }

    @DeleteMapping("/{id}")
    public void deleteMedium(@PathVariable Long id) {
        mediumService.eliminar(id);
    }

    @PutMapping("/{id}/descansar")
    public void descansar(@PathVariable Long id) {
        mediumService.descansar(id);
    }

    @PutMapping("/{idM}/invocar/espiritus/{idE}")
    public EspirituResponseDTO invocar(@PathVariable Long idM, @PathVariable Long idE) {
        Espiritu e = mediumService.invocar(idM, idE);
        return MapperEspiritu.toDTO(e);
    }

    @PutMapping("/{idM1}/exorcizar/mediums/{idM2}")
    public void exorcizar(@PathVariable Long idM1, @PathVariable Long idM2) {
        mediumService.exorcizar(idM1, idM2);
    }

    //acá necesitaría un method de MapperEspiritu que me devuelva un EspirituDTO
    @GetMapping("/{id}/espiritus")
//    public List<EspirituResponseDTO> espiritus(@PathVariable Long id) {
//        List<Espiritu> espiritus = mediumService.espiritus(id);
//        return espiritus.stream().
//                map((e) -> MapperEspiritu.toResumido(e)).
//                collect(Collectors.toList());
//    }

    public List<EspirituResponseDTO> espiritus(@PathVariable Long id) {
        List<Espiritu> espiritus = mediumService.espiritus(id);
        return espiritus.stream().
                map(MapperEspiritu::toDTO).toList();
    }

//    @PutMapping("/{idM}/mover/ubicaciones/{idU}")
//    public void mover(@PathVariable Long idM, @PathVariable Long idU) {
//
//    }

}
