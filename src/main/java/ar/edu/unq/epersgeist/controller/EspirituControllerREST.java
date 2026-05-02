package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.espiritus.EspirituCreateDTO;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/espiritus")
@AllArgsConstructor
public class EspirituControllerREST {
    private final EspirituService espirituService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EspirituResponseDTO crear(@Valid @RequestBody EspirituCreateDTO dto) {
        Espiritu espirituM = MapperEspiritu.crear(dto);
        Espiritu espirituP = espirituService.crear(espirituM);
        return MapperEspiritu.toDetalle(espirituP);
    }
}


