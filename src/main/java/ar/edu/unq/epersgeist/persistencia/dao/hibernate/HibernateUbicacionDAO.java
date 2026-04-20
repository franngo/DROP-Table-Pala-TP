package ar.edu.unq.epersgeist.persistencia.dao.hibernate;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateSessionContext;

public class HibernateUbicacionDAO implements UbicacionDAO {
    private final HibernateDAO<Ubicacion> hibernateDAO = new HibernateDAO<>(Ubicacion.class);

    @Override
    public Ubicacion guardar(Ubicacion ubicacion) {
        return this.hibernateDAO.crear(ubicacion);
    }

    @Override
    public Ubicacion recuperarPorNombre(String nombre) {
        return HibernateSessionContext.getCurrentSession()
                .createQuery("FROM Ubicacion WHERE nombre = :nombre", Ubicacion.class)
                .setParameter("nombre", nombre)
                .uniqueResult();
    }
}
