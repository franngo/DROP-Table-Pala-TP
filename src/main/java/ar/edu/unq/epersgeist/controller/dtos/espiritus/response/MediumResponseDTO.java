package ar.edu.unq.epersgeist.controller.dtos.espiritus.response;

public record MediumResponseDTO(
        Long id,
        String nombre,
        int manaMax,
        int mana
        //UbicacionDTO dtoU
        //List<EspirituDTO> dtosE
) {
}
