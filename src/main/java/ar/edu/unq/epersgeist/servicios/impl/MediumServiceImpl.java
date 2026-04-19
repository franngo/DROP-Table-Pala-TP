package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlRecuperarMediumException;
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
        return HibernateTransactionRunner.runTrx(() -> this.mediumDao.crear(medium) );
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
        } );
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
        return List.of();
    }

    @Override
    public void descanzar(Long id) {

    }

    @Override
    public void exorcizar(Long idatacante, Long idAtacado) {

    }

    @Override
    public List<Medium> espiritus() {
        return List.of();
    }

    @Override
    public Espiritu invocar(Long id, Long idMedium) {
        return null;
    }
}
