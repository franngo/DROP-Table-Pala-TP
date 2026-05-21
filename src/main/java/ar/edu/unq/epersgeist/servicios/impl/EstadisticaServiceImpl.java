package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.exceptions.NoExisteSantuarioCorruptoException;
import ar.edu.unq.epersgeist.modelo.reportes.ReporteSantuarioMasCorrupto;
import ar.edu.unq.epersgeist.persistencia.sql.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.sql.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.sql.UbicacionDAOSQL;
import ar.edu.unq.epersgeist.servicios.EstadisticaService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EstadisticaServiceImpl implements EstadisticaService {
    private final MediumDAO mediumDao;
    private final EspirituDAO espirituDao; //se requiere para usar en el invocar()
    private final UbicacionDAOSQL ubicacionDAOSQL;

    public EstadisticaServiceImpl(MediumDAO mediumDao, EspirituDAO espirituDao, UbicacionDAOSQL ubicacionDAOSQL) {
        this.mediumDao = mediumDao;
        this.espirituDao = espirituDao;
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
                espirituDao.demoniosLibres(santuario.getId());

        Medium medium =
                mediumDao
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


