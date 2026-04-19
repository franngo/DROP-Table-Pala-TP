package ar.edu.unq.epersgeist.persistencia.dao.hibernate;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;

public class HibernateMediumDAO extends HibernateDAO<Medium> implements MediumDAO {
    public HibernateMediumDAO() {
        super(Medium.class);
    }
}
