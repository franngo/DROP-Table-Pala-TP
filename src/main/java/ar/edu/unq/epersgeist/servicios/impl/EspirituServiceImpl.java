package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlConectarEspirituConMedium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDeEspirituNoEncontradoException;
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
                HibernateTransactionRunner.runTrx(() -> espirituDao.guardar(espiritu));
    }

    @Override
    public Espiritu recuperar(Long espirituId) {
        return HibernateTransactionRunner.runTrx(() -> {
            Espiritu espiritu = espirituDao.recuperar(espirituId);

            if (espiritu == null) {
                throw new ErrorDeEspirituNoEncontradoException(espirituId);
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
            if (espirituAConectar == null) {
                throw new ErrorAlConectarEspirituConMedium("El espiritu no existe en la bbdd");
            }
            if (mediumAConectar == null) {
                throw new ErrorAlConectarEspirituConMedium("El medium no existe en la bbdd");
            }
            if (!espirituAConectar.estaLibre()) {
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

    //versión que implementa la paginación
    @Override
    public List<Espiritu> espiritusDemoniacos(Direccion direccion, int pag, int cantPorPag) {
        return HibernateTransactionRunner.runTrx(() ->
                espirituDao.espiritusDemoniacos(direccion, pag, cantPorPag));
    }

    @Override

    public void eliminarTodos() {
        HibernateTransactionRunner.runTrx(() -> {
            espirituDao.eliminarTodos();
            return null;
        });
    }

}
