package ar.edu.unq.epersgeist.persistencia.dao.hibernate;

import ar.edu.unq.epersgeist.modelo.Medium;
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
    public void actualizar(Medium espiritu) {

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
