package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Ubicacion;

public interface UbicacionService {
    Ubicacion crear(String nombre);
    Ubicacion recuperar(Long id);
}
