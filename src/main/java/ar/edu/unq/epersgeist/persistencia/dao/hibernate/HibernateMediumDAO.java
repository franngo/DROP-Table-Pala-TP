package ar.edu.unq.epersgeist.persistencia.dao.hibernate;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlActualizarEspirituException;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateSessionContext;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class HibernateMediumDAO extends HibernateDAO<Medium> implements MediumDAO {
    public HibernateMediumDAO() {
        super(Medium.class);
    }

    @Override
    public void actualizar(Medium medium) {
        Session session = HibernateSessionContext.getCurrentSession();


        if (medium.getId() == null) {
            throw new ErrorAlActualizarEspirituException("No se puede actualizar un medium que no tiene id.");
        }

        Medium m = session.get(Medium.class, medium.getId());

        if (m == null) {
            throw new ErrorAlActualizarEspirituException("No existe en la BD el medium con id: " + medium.getId());
        }

        m.setNombre(medium.getNombre());
        m.setManaMax(medium.getManaMax());
        m.setMana(medium.getMana());
        m.setCantFortalece(medium.getCantFortalece());
        //m.setEspiritus(medium.getEspiritus());

        session.flush();
    }

    @Override
    public void eliminar(Long idDelMedium) {
        Session session = HibernateSessionContext.getCurrentSession();
        Medium medium = session.get(Medium.class, idDelMedium);
        if (medium == null) {
            return;
        }
        session.remove(medium);
    }

    @Override
    public List<Medium> recuperarTodos() {
        Session session = HibernateSessionContext.getCurrentSession();
        String hql = "select e from Medium e order by e.nombre asc";
        Query<Medium> query = session.createQuery(hql, Medium.class);
        return query.getResultList();
    }


}
