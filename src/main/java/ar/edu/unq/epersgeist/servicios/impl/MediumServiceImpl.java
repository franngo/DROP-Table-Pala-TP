package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.MediumService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class MediumServiceImpl implements MediumService {
    private final MediumDAO mediumDao;
    private final EspirituDAO espirituDao; //se requiere para usar en el invocar()
    private final UbicacionDAO ubicacionDAO;

    public MediumServiceImpl(MediumDAO mediumDao, EspirituDAO espirituDao, UbicacionDAO ubicacionDAO) {
        this.mediumDao = mediumDao;
        this.espirituDao = espirituDao;
        this.ubicacionDAO = ubicacionDAO;
    }

    @Override
    public Medium crear(Medium medium) {
        if (medium == null) {
            throw new ErrorAlCrearMediumException("medium nulo.");
        }

        if (medium.getId() != null) {
            throw new ErrorAlCrearMediumException("ya tiene un id asignado.");
        }

        return this.mediumDao.save(medium);
    }

    @Override
    public Medium recuperar(Long idMedium) {

        return mediumDao.findById(idMedium).orElseThrow(() -> new ErrorDeMediumNoEncontradoException(idMedium));
    }

    @Override
    public void actualizar(Medium medium) {
        if (medium.getId() == null) {
            throw new ErrorAlActualizarMediumException("No se puede actualizar un medium que no tiene id.");
        }

        //lo intento recuperar solo para saber si está o no en la DB y si debo tirar el error
        Long idMedium = medium.getId();
        mediumDao.findById(idMedium).orElseThrow(() -> new ErrorAlActualizarMediumException("No existe en la BD el medium con id: " + idMedium));

        this.mediumDao.save(medium);
    }

    @Override
    public void eliminar(Long idMedium) {
        mediumDao.deleteById(idMedium);
    }

    @Override
    public List<Medium> recuperarTodos() {
        return mediumDao.findAll();
    }

    @Override
    public void descansar(Long idMedium) {
        Medium m = this.recuperar(idMedium);
        m.descansar();
    }


    @Override
    public Espiritu invocar(Long mediumId, Long espirituId) {
        Medium m = this.recuperar(mediumId);

        Espiritu e = espirituDao.findById(espirituId).orElseThrow(() -> new ErrorDeEspirituNoEncontradoException(espirituId));

        return m.invocar(e);
    }

    @Override
    public void exorcizar(Long idMediumExorcista, Long idMediumAExorcizar) throws ExorcismoPropioNoPermitidoException {
        if (idMediumExorcista.equals(idMediumAExorcizar)) {
            throw new ExorcismoPropioNoPermitidoException("Un medium no puede exorcizarse a si mismo");
        }

        Medium mediumAtacante = this.recuperar(idMediumExorcista);
        Medium mediumAExorcizar = this.recuperar(idMediumAExorcizar);

        //en el service porque involucra ids, que es lo que usamos para comparar objetos por igualdad. Los recuperar anteriores nos aseguran los ids

        if (!mediumAtacante.getUbicacion().getId().equals(mediumAExorcizar.getUbicacion().getId())) {
            throw new ErrorDeMediumExorcizandoAOtroEnDistintaUbicacion(mediumAtacante.getId(), mediumAExorcizar.getId());
        }

        mediumAtacante.exorcizar(mediumAExorcizar);
    }


    @Override
    public List<Espiritu> espiritus(Long mediumId) {
        Medium recuperado = this.recuperar(mediumId);
        return recuperado.getEspiritus();
    }

    @Override
    public void mover(Long mediumId, Long ubicacionId) {
        Ubicacion ubicacionAMover = ubicacionDAO.findById(ubicacionId)
                .orElseThrow(() ->
                        new UbicacionNoEncontradaException(ubicacionId)
                );

        Medium mediumAMover = mediumDao.findById(mediumId)
                .orElseThrow(() ->
                        new ErrorDeMediumNoEncontradoException(mediumId)
                );
        Long ubiActual = mediumAMover.getUbicacion().getId();
        if (!quiereMoverseALaMisma(ubiActual, ubicacionId)) {
            mediumAMover.moverA(ubicacionAMover);
        }

        mediumDao.save(mediumAMover);

    }

    private boolean quiereMoverseALaMisma(Long idUbicacionActual, Long idUbicacionAMover) {
        return Objects.equals(idUbicacionActual, idUbicacionAMover);
    }

    @Override
    public void eliminarTodos() {
        mediumDao.deleteAll();
    }


}
