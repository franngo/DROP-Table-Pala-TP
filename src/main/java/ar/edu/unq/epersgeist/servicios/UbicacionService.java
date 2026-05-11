package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;

import java.util.List;

public interface UbicacionService {
    Ubicacion crear(Ubicacion ubicacion);

    Ubicacion recuperar(Long id);

    List<Ubicacion> recuperarTodos();

    void actualizar(Ubicacion ubicacion);

    void eliminar(Long id);

    void eliminarTodos();

    // --------------------------------------------------------------------------------

    List<Espiritu> espiritusEn(Long id);

    List<Medium> mediumsSinEspiritusEn(Long ubicacionId);
}
