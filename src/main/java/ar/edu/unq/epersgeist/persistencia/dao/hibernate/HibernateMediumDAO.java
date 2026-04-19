package ar.edu.unq.epersgeist.persistencia.dao.hibernate;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDao;

public class HibernateMediumDAO extends HibernateDAO<Medium> implements MediumDao {
    public HibernateMediumDAO() {
        super(Medium.class);
    }
}
