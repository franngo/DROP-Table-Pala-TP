package ar.edu.unq.epersgeist.controller.dtos.reportes.responses;

public record ReporteSantuarioMasCorruptoDTO(
        String nombreSantuario,
        String nombreMedium,
        Long cantidadTotalDemonios,
        Long demoniosLibres

) {
}
