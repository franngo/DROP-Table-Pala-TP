package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;

import java.util.List;

public interface UbicacionService {

    Ubicacion crear();
    void actualizar(Ubicacion ubicacion);
    public void recuperar(String nombreUbicacion);
    public void eliminar(String nombreUbicacion);
    List<Ubicacion> recuperarTodos();
    List<Espiritu> espiritusEn(Long idUbicacion);
    List<Medium> mediumsSinEspiritus(Long idUbicacion);
}
