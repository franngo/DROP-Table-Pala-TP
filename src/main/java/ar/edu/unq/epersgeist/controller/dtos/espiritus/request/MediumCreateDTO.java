package ar.edu.unq.epersgeist.controller.dtos.espiritus.request;

public record MediumCreateDTO(
        String nombre,
        int manaMax,
        int mana,
        Long ubicacionId
) {
}
