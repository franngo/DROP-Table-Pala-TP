package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;

import java.util.List;

public interface UbicacionService {
    Ubicacion crear(String nombre);
    Ubicacion recuperar(Long id);
    List<Ubicacion> recuperarTodos();
    List<Espiritu> espiritusEn(Long id);
    void actualizar(Ubicacion ubicacionMod);
    void eliminar(Long id);
    List<Medium> mediumsSinEspiritusEn(Long ubicacionId);
}
