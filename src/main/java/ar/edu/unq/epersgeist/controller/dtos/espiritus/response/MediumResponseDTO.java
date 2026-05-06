package ar.edu.unq.epersgeist.controller.dtos.espiritus.response;

public record MediumResponseDTO(
        Long id,
        String nombre,
        int mana,
        int manaMaximo
        //UbicacionDTO dtoU
        //List<EspirituDTO> dtosE
) {
}
