package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlRecuperarEspirituException;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;

import java.util.List;

public class EspirituServiceImpl implements EspirituService {
    private HibernateEspirituDAO espirituDao;

    public EspirituServiceImpl(HibernateEspirituDAO espirituDao) {
        this.espirituDao = espirituDao;
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
    public Medium conectar(Long espirituId, Medium medium) {
        Espiritu espirituAConectar = espirituDao.recuperar(espirituId);

        espirituAConectar.aumentarConexion(medium);

        espirituDao.actualizar(espirituAConectar);

        return medium;
    }

    @Override
    public List<Espiritu> espiritusDemoniacos() {
        return List.of();
    }

}
