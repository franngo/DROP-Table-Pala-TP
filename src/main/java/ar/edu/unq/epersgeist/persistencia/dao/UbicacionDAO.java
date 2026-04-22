package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;

public interface UbicacionDAO {
    Ubicacion crear(Ubicacion ubicacion);
    Ubicacion recuperar(Long idDeLaUbicacion);
    void eliminarTodos();
}
