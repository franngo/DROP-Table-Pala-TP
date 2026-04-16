package ar.edu.unq.epersgeist.persistencia.dao.hibernate;

import ar.edu.unq.epersgeist.servicios.runner.HibernateSessionContext;
import org.hibernate.Session;

public class HibernateDAO<T> {

    private final Class<T> entityType;

    public HibernateDAO(Class<T> entityType) {
        this.entityType = entityType;
    }

    public T crear(T entity) {
        Session session = HibernateSessionContext.getCurrentSession();
        session.save(entity);
        return entity;
    }

    public T recuperar(Long id) {
        Session session = HibernateSessionContext.getCurrentSession();
        return session.get(entityType, id);
    }

    public void eliminar(T entity) {
        Session session = HibernateSessionContext.getCurrentSession();
        session.remove(entity);
    }
    public void eliminarTodos() {
        Session session = HibernateSessionContext.getCurrentSession();
        session.createQuery("delete from " + entityType.getSimpleName()).executeUpdate();
    }
}