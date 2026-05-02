package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.espiritus.request.AngelicalCreateDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritus.request.DemonioCreateDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.controller.mappers.MapperEspiritu;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public EspirituResponseDTO obtener(Long id) {
        Espiritu espiritu = espirituService.recuperar(id);
        return MapperEspiritu.toDetalle(espiritu);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK) //TODO correjir codigo
    public List<EspirituResponseDTO> obtenerTodosLosItems() {
        List<Espiritu> todosE = espirituService.recuperarTodos();
        return todosE.stream().map(MapperEspiritu::toDetalle).toList();
    }

    @PutMapping("/{id}")
    public void

}


