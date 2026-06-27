package ar.edu.unq.epersgeist.persistencia.raven.cuervo;

import java.util.List;

public interface CuervoDaoRaven {
    Cuervo guardar(Cuervo cuervo);

    Cuervo actualizar(Cuervo cuervo);

    Cuervo buscarPorId(String id);

    List<Cuervo> recuperarTodos();

    void eliminar(String id);

    void eliminarTodos();
}