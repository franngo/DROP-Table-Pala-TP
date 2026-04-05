package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.jdbc.JDBCEspirituDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;

import java.util.List;

public class EspirituServiceImpl implements EspirituService {
    private JDBCEspirituDAO espirituDao;

    public EspirituServiceImpl(JDBCEspirituDAO espirituDao) {
        this.espirituDao = espirituDao;
    }

    @Override
    public Espiritu crear(Espiritu espiritu) {
        return espirituDao.crear(espiritu);
    }

    @Override
    public Espiritu recuperar(Long espirituId) {

        return espirituDao.recuperar(espirituId);

    }

    @Override
    public void actualizar(Espiritu espiritu) {
        // TODO completar
    }

    @Override
    public void eliminar(Long espirituId) {
        espirituDao.eliminar(espirituId);

    }

    @Override
    public List<Espiritu> recuperarTodos() {
        // TODO completar
        return null;
    }

    @Override
    public Medium conectar(Long espirituId, Medium medium) {
        // TODO completar
        return null;
    }
}
