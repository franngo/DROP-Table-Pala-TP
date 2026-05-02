package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.espiritus.request.AngelicalCreateDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritus.request.AngelicalUpdateDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritus.request.DemonioCreateDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritus.request.DemonioUpdateDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.controller.mappers.MapperEspiritu;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/espiritus")
@AllArgsConstructor
public class EspirituControllerREST {
    private final EspirituService espirituService;
    private final UbicacionService ubicacionService;

    @PostMapping("/demonio")
    @ResponseStatus(HttpStatus.CREATED)
    public EspirituResponseDTO crearDemonio(@Valid @RequestBody DemonioCreateDTO dto) {
        Ubicacion ubicacion = ubicacionService.recuperar(dto.ubicacionId());


        Espiritu espirituM = MapperEspiritu.crearDemonio(dto, ubicacion);
        Espiritu espirituP = espirituService.crear(espirituM);
        return MapperEspiritu.toDetalle(espirituP);
    }

    @PostMapping("/angel")
    @ResponseStatus(HttpStatus.CREATED)
    public EspirituResponseDTO crearAngel(@Valid @RequestBody AngelicalCreateDTO dto) {
        Ubicacion ubicacion = ubicacionService.recuperar(dto.ubicacionId());


        Espiritu espirituM = MapperEspiritu.crearAngelical(dto, ubicacion);
        Espiritu espirituP = espirituService.crear(espirituM);
        return MapperEspiritu.toDetalle(espirituP);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EspirituResponseDTO obtener(@PathVariable Long id) {
        Espiritu espiritu = espirituService.recuperar(id);
        return MapperEspiritu.toDetalle(espiritu);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK) //TODO correjir codigo
    public List<EspirituResponseDTO> obtenerTodosLosItems() {
        List<Espiritu> todosE = espirituService.recuperarTodos();
        return todosE.stream().map(MapperEspiritu::toDetalle).toList();
    }

    @PutMapping("/demonio/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void actualizarDemonio(@Valid @RequestBody DemonioUpdateDTO dto, @PathVariable Long id) {

        Espiritu espirituP = espirituService.recuperar(id);

        MapperEspiritu.actualizarDemonio((Demonio) espirituP, dto);

        espirituService.actualizar(espirituP);


    }

    @PutMapping("/angelical/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void actualizarAngelical(@Valid @RequestBody AngelicalUpdateDTO dto, @PathVariable Long id) {

        Espiritu espirituP = espirituService.recuperar(id);

        MapperEspiritu.actualizarAngelical((Angelical) espirituP, dto);

        espirituService.actualizar(espirituP);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void eliminar(@PathVariable Long id) {
        espirituService.eliminar(id);
    }

    //CASOS DE USO

}


