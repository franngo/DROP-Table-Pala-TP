package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.PaginacionEspirituResponseDTO;
import ar.edu.unq.epersgeist.controller.dtos.espiritus.request.EspirituCreateDTO;
import ar.edu.unq.epersgeist.controller.dtos.espiritus.request.EspirituUpdateDTO;
import ar.edu.unq.epersgeist.controller.dtos.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.controller.mappers.MapperEspiritu;
import ar.edu.unq.epersgeist.controller.mappers.PageMapper;
import ar.edu.unq.epersgeist.modelo.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/espiritus")
@AllArgsConstructor
public class EspirituControllerREST {
    private final EspirituService espirituService;
    private final UbicacionService ubicacionService;

    @PostMapping("/crear")
    @ResponseStatus(HttpStatus.CREATED)
    public EspirituResponseDTO crearEspiritu(@Valid @RequestBody EspirituCreateDTO dto) {
        Ubicacion ubicacion = ubicacionService.recuperar(dto.ubicacionId());


        Espiritu espirituM = MapperEspiritu.toModelo(dto, ubicacion);
        Espiritu espirituP = espirituService.crear(espirituM);
        return MapperEspiritu.toDTO(espirituP);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EspirituResponseDTO obtener(@PathVariable Long id) {
        Espiritu espiritu = espirituService.recuperar(id);
        return MapperEspiritu.toDTO(espiritu);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EspirituResponseDTO> obtenerTodosLosEspiritus() {
        List<Espiritu> todosE = espirituService.recuperarTodos();
        return todosE.stream().map(MapperEspiritu::toDTO).toList();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void actualizarEspiritu(@Valid @RequestBody EspirituUpdateDTO dto, @PathVariable Long id) {

        Espiritu espirituP = espirituService.recuperar(id);

        MapperEspiritu.actualizarEspiritu(espirituP, dto);

        espirituService.actualizar(espirituP);


    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void eliminar(@PathVariable Long id) {
        espirituService.eliminar(id);
    }


    @PostMapping("/{espirituId}/conectar/{mediumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void conectar(@PathVariable Long espirituId,
                         @PathVariable Long mediumId) {

        espirituService.conectar(espirituId, mediumId);
    }

    @GetMapping("/espiritus/demoniacos")
    @ResponseStatus(HttpStatus.OK)
    public PaginacionEspirituResponseDTO espiritusDemoniacos(
            @RequestParam Direccion direccion,
            @RequestParam int pagina,
            @RequestParam int cantidadPorPagina
    ) {
        Page<Espiritu> paginada = espirituService.espiritusDemoniacos(direccion, pagina, cantidadPorPagina);
        return PageMapper.toEspirituPage(paginada,
                MapperEspiritu::toDTO,
                direccion);
    }

}


