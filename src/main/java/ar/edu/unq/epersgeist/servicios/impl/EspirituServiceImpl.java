package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlConectarEspirituConMedium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlRecuperarEspirituException;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;

import java.util.List;

public class EspirituServiceImpl implements EspirituService {
    private EspirituDAO espirituDao;

    private MediumDAO mediumDao;

    public EspirituServiceImpl(EspirituDAO espirituDao, MediumDAO mediumDao) {

        this.espirituDao = espirituDao;
        this.mediumDao = mediumDao;
    }

    @Override
    public Espiritu crear(Espiritu espiritu) {
        return
                HibernateTransactionRunner.runTrx(() -> espirituDao.crear(espiritu));
    }

    @Override
    public Espiritu recuperar(Long espirituId) {
        return HibernateTransactionRunner.runTrx(() -> {
            Espiritu espiritu = espirituDao.recuperar(espirituId);

            if (espiritu == null) {
                throw new ErrorAlRecuperarEspirituException(espirituId);
            }

            return espiritu;
        });
    }

    @Override
    public void actualizar(Espiritu espiritu) {
        HibernateTransactionRunner.runTrx(() -> {
            espirituDao.actualizar(espiritu);
            return null;
        });
    }

    @Override
    public void eliminar(Long espirituId) {
        HibernateTransactionRunner.runTrx(() -> {
            espirituDao.eliminar(espirituId);
            return null;
        });
    }

    @Override
    public List<Espiritu> recuperarTodos() {
        return HibernateTransactionRunner.runTrx(() ->
                espirituDao.recuperarTodos());
    }

    @Override
    public Medium conectar(Long espirituId, Long mediumId) {
        return HibernateTransactionRunner.runTrx(() -> {
            Espiritu espirituAConectar = espirituDao.recuperar(espirituId);
            Medium mediumAConectar = mediumDao.recuperar(mediumId);

            if (!espirituAConectar.getLibre()) {
                throw new ErrorAlConectarEspirituConMedium("El espiritu no se encuentra libre");
            }
            if (!espirituAConectar.estaUbicadoEnElMismoLugarQue(mediumAConectar)) {
                throw new ErrorAlConectarEspirituConMedium("El espiritu se encuentra en una ubicacion diferente al medium");
            }

            espirituAConectar.aumentarConexionYConectar(mediumAConectar);

            mediumDao.actualizar(mediumAConectar);

            return mediumAConectar;
        });
    }

    @Override
    public List<Espiritu> espiritusDemoniacos() {
        return HibernateTransactionRunner.runTrx(() ->
                espirituDao.espiritusDemoniacos());
    }

    @Override

    public void eliminarTodos() {
        HibernateTransactionRunner.runTrx(() -> {
            espirituDao.eliminarTodos();
            return null;
        });
    }


}
