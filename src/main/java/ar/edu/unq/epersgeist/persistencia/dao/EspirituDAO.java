package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Espiritu;

import java.util.List;

public interface EspirituDAO {
    Espiritu crear(Espiritu espiritu);

    Espiritu recuperar(Long idDelEspiritu);

    //    void actualizar(Espiritu espiritu);
    void eliminar(Long idDelEspiritu);

    List<Espiritu> recuperarTodos();

    //agrego para testear
    void eliminarTodos();
}