package ar.edu.unq.epersgeist.controller.dtos.mediums.response;

public record MediumDTO(
        Long id,
        String nombre,
        int manaMax,
        int mana
) {
}
