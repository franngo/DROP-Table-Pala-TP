package ar.edu.unq.epersgeist.persistencia.raven.cuervo;

import org.springframework.stereotype.Repository;

@Repository
public interface CuervoDaoRaven {

    Cuervo crear(String nombre);
}
