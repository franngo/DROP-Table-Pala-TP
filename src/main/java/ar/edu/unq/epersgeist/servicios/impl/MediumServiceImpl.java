package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlRecuperarMediumException;
import ar.edu.unq.epersgeist.modelo.exceptions.ExorcismoPropioNoPermitidoException;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;

import java.util.List;

//TODO IMPLEMENTAR
/*
 *
 * Servicio Medium
 * */
public class MediumServiceImpl implements MediumService {
    private MediumDAO mediumDao;

    public MediumServiceImpl(MediumDAO mediumDao) {
        this.mediumDao = mediumDao;
    }

    @Override
    public Medium crear(Medium medium) {
        return HibernateTransactionRunner.runTrx(() -> this.mediumDao.crear(medium));
    }

    @Override
    public Medium recuperar(Long idMedium) {
        return HibernateTransactionRunner.runTrx(() -> {
            Medium mediumRecuperado = mediumDao.recuperar(idMedium);

            if (mediumRecuperado == null) {
                throw new ErrorAlRecuperarMediumException(idMedium);
            }

            return mediumRecuperado;
        });
    }

    @Override
    public void actualizar(Medium medium) {
        HibernateTransactionRunner.runTrx(() -> {
            this.mediumDao.actualizar(medium);
            return null;
        });
    }

    @Override
    public void eliminar(Long idMedium) {
        HibernateTransactionRunner.runTrx(() -> {
            mediumDao.eliminar(idMedium);
            return null;
        });
    }

    @Override
    public List<Medium> recuperarTodos() {
        return HibernateTransactionRunner.runTrx(() ->
                mediumDao.recuperarTodos());
    }

    @Override
    public void descansar(Long idMedium) {

    }

    @Override
    public void exorcizar(Long idMediumExorcista, Long idMediumAExorcizar) throws ExorcismoPropioNoPermitidoException {
        if (idMediumExorcista == idMediumAExorcizar) {
            throw new ExorcismoPropioNoPermitidoException("Un medium no puede exorcizarse a si mismo");
        }
        HibernateTransactionRunner.runTrx(() -> {
            mediumDao.exorcizar(idMediumExorcista, idMediumAExorcizar);
            return null;
        });

    }

    @Override
    public List<Espiritu> espiritus(Long mediumId) {
        return List.of();
    }

    @Override
    public Espiritu invocar(Long mediumId, Long espirituId) {
        return null;
    }


}
