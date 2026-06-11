package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.HarmonicResult;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.medium.Medium;

import java.util.List;

public interface UbicacionService {
    Ubicacion crear(Ubicacion ubicacion, List<Coordenada> coordenadas);

    Ubicacion recuperar(Long id);

    List<Ubicacion> recuperarTodos();

    void actualizar(Ubicacion ubicacion);

    void eliminar(Long id);

    void eliminarTodos();

    // --------------------------------------------------------------------------------
    // CONSULTAS SQL
    // --------------------------------------------------------------------------------

    List<Espiritu> espiritusEn(Long id);

    List<Medium> mediumsSinEspiritusEn(Long ubicacionId);

    // --------------------------------------------------------------------------------
    // CONSULTAS NEO
    // --------------------------------------------------------------------------------

    void conectar(Long idOrigen, Long idDestino, Long costo);

    Boolean estanConectadas(Long idOrigen, Long idDestino);

    List<Ubicacion> caminoMasRentable(Long idOrigen, Long idDestino);

    List<Ubicacion> caminoMasCorto(Long idOrigen, Long idDestino);

    List<Ubicacion> ubicacionesSobrecargadas(int umbralDeEnergia);

    List<HarmonicResult> harmonicResult(List<Long> ids);

    List<String> ubicacionesFrias();

}
