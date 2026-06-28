package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.persistencia.raven.Cuervo;

import java.util.List;


public interface CuervoService {
    Cuervo crear(String nombre);

    Cuervo recuperar(Long id);

    List<Cuervo> recuperarTodos();

    void actualizar(Cuervo cuervo);

    void eliminar(Long id);

    void eliminarTodos();

    List<Cuervo> recuperarHistorial(Long id);
}
