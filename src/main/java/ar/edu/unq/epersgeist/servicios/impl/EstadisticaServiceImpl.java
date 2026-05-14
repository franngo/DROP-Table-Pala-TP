package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.exceptions.NoExisteSantuarioCorruptoException;
import ar.edu.unq.epersgeist.modelo.reportes.ReporteSantuarioMasCorrupto;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.EstadisticaService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EstadisticaServiceImpl implements EstadisticaService {
    private final MediumDAO mediumDao;
    private final EspirituDAO espirituDao; //se requiere para usar en el invocar()
    private final UbicacionDAO ubicacionDAO;

    public EstadisticaServiceImpl(MediumDAO mediumDao, EspirituDAO espirituDao, UbicacionDAO ubicacionDAO) {
        this.mediumDao = mediumDao;
        this.espirituDao = espirituDao;
        this.ubicacionDAO = ubicacionDAO;
    }

    public ReporteSantuarioMasCorrupto santuarioCorrupto() {

        Santuario santuario =
                ubicacionDAO
                        .santuariosMasCorruptos(PageRequest.of(0, 1))
                        .stream()
                        .findFirst()
                        .orElseThrow(
                                () -> new NoExisteSantuarioCorruptoException("No existe un santuario corrupto")
                        );

        Long cantidadTotalDemonios =
                ubicacionDAO.cantidadDemonios(santuario.getId());

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


