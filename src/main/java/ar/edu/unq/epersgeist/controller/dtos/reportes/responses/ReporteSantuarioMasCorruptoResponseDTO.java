package ar.edu.unq.epersgeist.controller.dtos.reportes.responses;

public record ReporteSantuarioMasCorruptoResponseDTO(
        String nombreSantuario,
        String nombreMedium,
        Long cantidadTotalDemonios,
        Long demoniosLibres

) {
}
