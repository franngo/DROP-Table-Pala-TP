package ar.edu.unq.epersgeist.controller.dtos.espiritus.response;

public record MediumDTO(
        Long id,
        String nombre,
        int manaMax,
        int mana
) {
}
