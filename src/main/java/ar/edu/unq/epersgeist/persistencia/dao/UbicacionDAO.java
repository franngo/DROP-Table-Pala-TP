package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;

import java.util.List;

public interface UbicacionDAO {
    Ubicacion guardar(Ubicacion ubicacion);
    void actualizar(Ubicacion ubicacion);
    Ubicacion recuperar(Long id);
    Ubicacion recuperarPorNombre(String nombre);
    List<Ubicacion> recuperarTodos();
    void eliminarTodos();
    void eliminar(Long ubicacionId);
}
