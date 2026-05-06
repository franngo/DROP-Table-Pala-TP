package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.UbicacionCreateDTO;
import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.UbicacionUpdateDTO;
import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.response.UbicacionResponseDTO;
import ar.edu.unq.epersgeist.controller.mappers.MapperUbicacion;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ubicaciones")
@AllArgsConstructor
public class UbicacionControllerREST {
    private final UbicacionService ubicacionService;

    // --------------------------------------------------------------------------------

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UbicacionResponseDTO> obtenerTodas() {
        List<Ubicacion> ubicaciones = ubicacionService.recuperarTodos();
        return ubicaciones.stream()
                .map(MapperUbicacion::toDTO)
                .toList();
    }

    // --------------------------------------------------------------------------------

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UbicacionResponseDTO obtenerPorId(@PathVariable Long id) {
        Ubicacion ubicacion = ubicacionService.recuperar(id);
        return MapperUbicacion.toDTO(ubicacion);
    }

    // --------------------------------------------------------------------------------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UbicacionResponseDTO crear(@Valid @RequestBody UbicacionCreateDTO dto) {
        Ubicacion ubicacion = new Ubicacion(dto.nombre());
        Ubicacion ubicacionCreada = ubicacionService.crear(ubicacion);
        return MapperUbicacion.toDTO(ubicacionCreada);
    }

    // --------------------------------------------------------------------------------

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UbicacionUpdateDTO dto
    ) {
        Ubicacion ubicacionRecuperada = ubicacionService.recuperar(id);
        MapperUbicacion.actualizar(ubicacionRecuperada, dto);
        ubicacionService.actualizar(ubicacionRecuperada);
    }

    // --------------------------------------------------------------------------------
}
