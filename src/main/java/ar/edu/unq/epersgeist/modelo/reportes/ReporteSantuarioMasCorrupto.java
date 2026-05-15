package ar.edu.unq.epersgeist.modelo.reportes;

import lombok.Getter;

@Getter
public class ReporteSantuarioMasCorrupto {
    private String nombreSantuario;
    private String nombreMedium;
    private Long cantidadTotalDemonios;
    private Long demoniosLibres;

    public ReporteSantuarioMasCorrupto(String nombreSantuario, String nombreMedium, Long demonios, Long libres) {
        this.nombreSantuario = nombreSantuario;
        this.nombreMedium = nombreMedium;
        this.cantidadTotalDemonios = demonios;
        this.demoniosLibres = libres;
    }
}
