package ar.edu.unq.epersgeist.persistencia.dao.hibernate;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlActualizarEspirituException;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateSessionContext;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class HibernateEspirituDAO extends HibernateDAO<Espiritu> implements EspirituDAO {
    public HibernateEspirituDAO() {
        super(Espiritu.class);
    }

    @Override
    public Espiritu guardar(Espiritu espiritu) {
        return super.crear(espiritu);
    }

    @Override
    public Espiritu recuperar(Long id) {
        return super.recuperar(id);
    }

    @Override
    public void eliminarTodos() {
        super.eliminarTodos();
    }

    @Override
    public void actualizar(Espiritu espiritu) {
        Session session = HibernateSessionContext.getCurrentSession();

        if (espiritu.getId() == null) {
            throw new ErrorAlActualizarEspirituException("No se puede actualizar un espiritu sin id");
        }

        Espiritu persistido = session.get(Espiritu.class, espiritu.getId());

        if (persistido == null) {
            throw new ErrorAlActualizarEspirituException("No existe el espiritu con id: " + espiritu.getId());
        }


        persistido.setNombre(espiritu.getNombre());
        persistido.setNivelDeConexion(espiritu.getNivelDeConexion());

        session.flush();
    }

    @Override
    public void eliminar(Long id) {
        /* Se hace de esta manera por un tema de contexto de la sesión,
        cuando dentro de una misma transacción haces crear, eliminar y recuperar
        La 1st caché de Hibernate (que no podemos desactivar en el hibernate.cfg.xml)
        todavía tiene a la entidad en memoria. Soportamos hacer un select y posteriormente
        el delete para mantener la consistencia dentro de una sesión -dpolverigiani */

        Session session = HibernateSessionContext.getCurrentSession();
        Espiritu esp = session.get(Espiritu.class, id);
        if (esp == null) {
            return;
        }
        session.remove(esp);
    }

    @Override
    public List<Espiritu> recuperarTodos() {
        Session session = HibernateSessionContext.getCurrentSession();
        String hql = "select e from Espiritu e order by e.nombre asc";
        Query<Espiritu> query = session.createQuery(hql, Espiritu.class);
        return query.getResultList();
    }


}
