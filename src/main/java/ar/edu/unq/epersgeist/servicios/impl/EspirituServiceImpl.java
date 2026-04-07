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
        espirituDao.actualizar(espiritu);
    }

    @Override
    public void eliminar(Long espirituId) {
        espirituDao.eliminar(espirituId);

    }

    @Override
    public List<Espiritu> recuperarTodos() {
        return espirituDao.recuperarTodos();
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

}
