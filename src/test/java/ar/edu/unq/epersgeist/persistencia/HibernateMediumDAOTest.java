package ar.edu.unq.epersgeist.persistencia;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.hibernate.HibernateMediumDAO;
import org.junit.jupiter.api.BeforeEach;

public class HibernateMediumDAOTest {

    MediumDAO mediumDao = new HibernateMediumDAO();
    Medium mediumM;

    @BeforeEach
    void setUp() {
        mediumM = new Medium("Matias", 200, 100);
    }


    //test recuperar y eliminar
    
}
