package ar.edu.unq.epersgeist.persistencia.dao.hibernate;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.ErrorAlActualizarUbicacionException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.IdUbicacionInvalidoException;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateSessionContext;
import org.hibernate.Session;

import java.util.List;

public class HibernateUbicacionDAO implements UbicacionDAO {
    private final HibernateDAO<Ubicacion> hibernateDAO = new HibernateDAO<>(Ubicacion.class);

    @Override
    public Ubicacion guardar(Ubicacion ubicacion) {
        return this.hibernateDAO.crear(ubicacion);
    }

    @Override
    public void actualizar(Ubicacion ubicacion) {
        Session session = HibernateSessionContext.getCurrentSession();

        if (ubicacion == null || ubicacion.getId() == null){
            throw new ErrorAlActualizarUbicacionException("No se puede actualizar la ubicacion que no tiene id.");
        }

        Ubicacion u = session.get(Ubicacion.class, ubicacion.getId());

        if (u == null) {
            throw new ErrorAlActualizarUbicacionException("No existe en la BD la ubicacion con id: " + ubicacion.getId());
        }

        u.setNombre(ubicacion.getNombre());
        u.setEspiritus(ubicacion.getEspiritus());
        u.setMediums(ubicacion.getMediums());
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

    @Override
    public void eliminar(Long ubicacionId){
        Session session = HibernateSessionContext.getCurrentSession();
        Ubicacion ubicacion = session.get(Ubicacion.class, ubicacionId);
        if (ubicacion == null) {
            return;
        }
        session.remove(ubicacion);
    }
}
