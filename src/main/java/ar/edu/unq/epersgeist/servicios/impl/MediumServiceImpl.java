package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.servicios.MediumService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MediumServiceImpl implements MediumService {
    private final MediumDAO mediumDao;
    private final EspirituDAO espirituDao; //se requiere para usar en el invocar()

    public MediumServiceImpl(MediumDAO mediumDao, EspirituDAO espirituDao) {
        this.mediumDao = mediumDao;
        this.espirituDao = espirituDao;
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
        if (idMedium == null) {
            throw new IdMediumInvalidoException();
        }

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

        if (espirituId == null) {
            throw new IdEspirituInvalidoException();
        }


        Espiritu e = espirituDao.findById(espirituId).orElseThrow(() -> new ErrorDeEspirituNoEncontradoException(espirituId));

        if (e == null) {
            throw new ErrorDeEspirituNoEncontradoException(espirituId);
        }

        return m.invocar(e);
    }

    @Override
    public void exorcizar(Long idMediumExorcista, Long idMediumAExorcizar) throws ExorcismoPropioNoPermitidoException {
        if (idMediumExorcista.equals(idMediumAExorcizar)) {
            throw new ExorcismoPropioNoPermitidoException("Un medium no puede exorcizarse a si mismo");
        }

        Medium mediumAtacante = this.recuperar(idMediumExorcista);
        Medium mediumAExorcizar = this.recuperar(idMediumAExorcizar);

        mediumAtacante.exorcizar(mediumAExorcizar);
    }


    @Override
    public List<Espiritu> espiritus(Long mediumId) {
        Medium recuperado = this.recuperar(mediumId);
        return new ArrayList<Espiritu>(recuperado.getEspiritus());
    }


    @Override
    public void eliminarTodos() {
        mediumDao.deleteAll();
    }


}
