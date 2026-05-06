package ar.edu.unq.epersgeist.controller.dtos.espiritus.request;

public record MediumUpdateDTO(
        String nombre,
        int manaMax,
        int mana
) {
}
