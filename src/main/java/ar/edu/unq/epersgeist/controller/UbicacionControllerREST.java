package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.response.EspirituResponseDTO;
import ar.edu.unq.epersgeist.controller.dtos.mediums.response.MediumResponseDTO;
import ar.edu.unq.epersgeist.controller.dtos.reportes.responses.ReporteSantuarioMasCorruptoResponseDTO;
import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.UbicacionCreateDTO;
import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.UbicacionUpdateDTO;
import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.response.UbicacionResponseDTO;
import ar.edu.unq.epersgeist.controller.mappers.EstadisticaMapper;
import ar.edu.unq.epersgeist.controller.mappers.MapperEspiritu;
import ar.edu.unq.epersgeist.controller.mappers.MapperMedium;
import ar.edu.unq.epersgeist.controller.mappers.MapperUbicacion;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.reportes.ReporteSantuarioMasCorrupto;
import ar.edu.unq.epersgeist.servicios.EstadisticaService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ubicaciones")
@AllArgsConstructor
@Validated
public class UbicacionControllerREST {
    private final UbicacionService ubicacionService;
    private final EstadisticaService estadisticaService;

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
        Ubicacion ubicacion = MapperUbicacion.toModelo(dto);
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void eliminar(@PathVariable Long id) {
        ubicacionService.eliminar(id);
    }

    // --------------------------------------------------------------------------------

    @GetMapping("/{id}/espiritus")
    @ResponseStatus(HttpStatus.OK)
    public List<EspirituResponseDTO> obtenerEspiritus(@PathVariable Long id) {
        return ubicacionService.espiritusEn(id)
                .stream()
                .map(MapperEspiritu::toDTO)
                .toList();
    }

    // --------------------------------------------------------------------------------

    @GetMapping("/{id}/mediums/sin-espiritus")
    @ResponseStatus(HttpStatus.OK)
    public List<MediumResponseDTO> obtenerMediumsSinEspiritus(@PathVariable Long id) {
        return ubicacionService.mediumsSinEspiritusEn(id)
                .stream()
                .map(MapperMedium::toDTO)
                .toList();
    }

    // --------------------------------------------------------------------------------

    @GetMapping("/santuario-corrupto")
    @ResponseStatus(HttpStatus.OK)
    public ReporteSantuarioMasCorruptoResponseDTO santuarioMasCorrupto() {

        ReporteSantuarioMasCorrupto reporte = estadisticaService.santuarioCorrupto();

        return EstadisticaMapper.toDTO(reporte);
    }

    // --------------------------------------------------------------------------------

    @PostMapping("/{idOrigen}/conectar/{idDestino}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void conectar(
            @PathVariable Long idOrigen,
            @PathVariable Long idDestino,
            @RequestParam @Min(0) @Max(100) Long costo
     ) {
        ubicacionService.conectar(idOrigen, idDestino, costo);
    }

    // --------------------------------------------------------------------------------

    @GetMapping("/{idOrigen}/camino-mas-corto/{idDestino}")
    @ResponseStatus(HttpStatus.OK)
    public List<UbicacionResponseDTO> caminoMasCorto(
            @PathVariable Long idOrigen,
            @PathVariable Long idDestino
    ) {
        return ubicacionService.caminoMasCorto(idOrigen, idDestino)
                .stream()
                .map(MapperUbicacion::toDTO)
                .toList();
    }

    // --------------------------------------------------------------------------------

    @GetMapping("/{idOrigen}/estan-conectadas/{idDestino}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean estanConectadas(
            @PathVariable Long idOrigen,
            @PathVariable Long idDestino
    ) {
        return ubicacionService.estanConectadas(idOrigen, idDestino);
    }

    @GetMapping("/sobrecargadas/{umbral}")
    @ResponseStatus(HttpStatus.OK)
    public List<UbicacionResponseDTO> ubicacionSobrecargada(@PathVariable int umbral){
        return ubicacionService.ubicacionesSobrecargadas(umbral)
                .stream()
                .map(MapperUbicacion::toDTO)
                .toList();
    }
}

