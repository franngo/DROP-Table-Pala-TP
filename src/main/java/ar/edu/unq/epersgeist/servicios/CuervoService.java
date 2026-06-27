package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.persistencia.raven.cuervo.Cuervo;

import java.util.List;

public interface CuervoService {
    Cuervo crear(String nombre);

    Cuervo actualizar(Cuervo cuervo);

    Cuervo obtenerPorId(String id);

    List<Cuervo> obtenerTodos();

    void eliminar(String id);

    void eliminarTodos();
}