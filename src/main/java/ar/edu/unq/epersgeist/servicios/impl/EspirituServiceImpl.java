package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlRecuperarEspirituException;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.hibernate.Hibernate;

import java.util.List;

public class EspirituServiceImpl implements EspirituService {
    private HibernateEspirituDAO espirituDao;
    private HibernateMediumDAO mediumDao;

    public EspirituServiceImpl(HibernateEspirituDAO espirituDao, HibernateMediumDAO mediumDao) {
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

    /*
     * Paso 1: pedir el espiritu a la BBDD
     * Paso 2: aplicar logica de negocio entre MODELOS
     * Paso 3: persistir los cambios
     * */
    @Override
    public Medium conectar(Long espirituId, Long mediumId) {
        Espiritu espirituAConectar = espirituDao.recuperar(espirituId);
        Medium mediumAConectar = mediumDao.recuperar(mediumId);

        espirituAConectar.aumentarConexion(mediumAConectar);

        espirituDao.actualizar(espirituAConectar);

        return mediumAConectar;
    }

    @Override
    public List<Espiritu> espiritusDemoniacos() {
        return HibernateTransactionRunner.runTrx(() ->
                espirituDao.espiritusDemoniacos());
    }
}
