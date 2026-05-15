package ar.edu.unq.epersgeist.controller.mappers;

import ar.edu.unq.epersgeist.controller.dtos.reportes.responses.ReporteSantuarioMasCorruptoResponseDTO;
import ar.edu.unq.epersgeist.modelo.reportes.ReporteSantuarioMasCorrupto;

public class EstadisticaMapper {
    public static ReporteSantuarioMasCorruptoResponseDTO toDTO(ReporteSantuarioMasCorrupto reporte) {

        return new ReporteSantuarioMasCorruptoResponseDTO(
                reporte.getNombreSantuario(),
                reporte.getNombreMedium(),
                reporte.getCantidadTotalDemonios(),
                reporte.getDemoniosLibres()
        );
    }
}
