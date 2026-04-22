package ar.edu.unq.epersgeist.persistencia.dao.hibernate;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateSessionContext;

import java.util.List;

public class HibernateUbicacionDAO implements UbicacionDAO {
    private final HibernateDAO<Ubicacion> hibernateDAO = new HibernateDAO<>(Ubicacion.class);

    @Override
    public Ubicacion guardar(Ubicacion ubicacion) {
        return this.hibernateDAO.crear(ubicacion);
    }

    @Override
    public Ubicacion recuperar(Long id) {
        return this.hibernateDAO.recuperar(id);
    }

    @Override
    public List<Ubicacion> recuperarTodos() {
        return HibernateSessionContext.getCurrentSession()
                .createQuery("FROM Ubicacion", Ubicacion.class)
                .list();
    }

    @Override
    public Ubicacion recuperarPorNombre(String nombre) {
        return HibernateSessionContext.getCurrentSession()
                .createQuery("FROM Ubicacion WHERE nombre = :nombre", Ubicacion.class)
                .setParameter("nombre", nombre)
                .uniqueResult();
    }

    @Override
    public void eliminarTodos() {
        this.hibernateDAO.eliminarTodos();
    }
}
