package ar.edu.unq.epersgeist.controller.mappers;

import ar.edu.unq.epersgeist.controller.dtos.reportes.responses.ReporteSantuarioMasCorruptoDTO;
import ar.edu.unq.epersgeist.modelo.reportes.ReporteSantuarioMasCorrupto;

public class EstadisticaMapper {
    public static ReporteSantuarioMasCorruptoDTO toDTO(ReporteSantuarioMasCorrupto reporte) {


        return new ReporteSantuarioMasCorruptoDTO(
                reporte.getNombreSantuario(),
                reporte.getNombreMedium(),
                reporte.getCantidadTotalDemonios(),
                reporte.getDemoniosLibres()
        );
    }
}
