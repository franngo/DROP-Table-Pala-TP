package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.exceptions.NoExisteSantuarioCorruptoException;
import ar.edu.unq.epersgeist.modelo.reportes.ReporteSantuarioMasCorrupto;
import ar.edu.unq.epersgeist.persistencia.sql.EspirituDAOSQL;
import ar.edu.unq.epersgeist.persistencia.sql.MediumDAOSQL;
import ar.edu.unq.epersgeist.persistencia.sql.UbicacionDAOSQL;
import ar.edu.unq.epersgeist.servicios.EstadisticaService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EstadisticaServiceImpl implements EstadisticaService {
    private final MediumDAOSQL mediumDAOSQL;
    private final EspirituDAOSQL espirituDAOSQL; //se requiere para usar en el invocar()
    private final UbicacionDAOSQL ubicacionDAOSQL;

    public EstadisticaServiceImpl(MediumDAOSQL mediumDAOSQL, EspirituDAOSQL espirituDAOSQL, UbicacionDAOSQL ubicacionDAOSQL) {
        this.mediumDAOSQL = mediumDAOSQL;
        this.espirituDAOSQL = espirituDAOSQL;
        this.ubicacionDAOSQL = ubicacionDAOSQL;
    }

    public ReporteSantuarioMasCorrupto santuarioCorrupto() {

        Santuario santuario =
                ubicacionDAOSQL
                        .santuariosMasCorruptos(PageRequest.of(0, 1))
                        .stream()
                        .findFirst()
                        .orElseThrow(
                                () -> new NoExisteSantuarioCorruptoException("No existe un santuario corrupto")
                        );

        Long cantidadTotalDemonios =
                ubicacionDAOSQL.cantidadDemonios(santuario.getId());

        Long cantLibres =
                espirituDAOSQL.demoniosLibres(santuario.getId());

        Medium medium =
                mediumDAOSQL
                        .mediumConMasDemonios(
                                santuario.getId(),
                                PageRequest.of(0, 1)
                        )
                        .stream()
                        .findFirst()
                        .orElse(null);

        return new ReporteSantuarioMasCorrupto(
                santuario.getNombre(),
                medium != null ? medium.getNombre() : null,
                cantidadTotalDemonios,
                cantLibres

        );
    }
}


