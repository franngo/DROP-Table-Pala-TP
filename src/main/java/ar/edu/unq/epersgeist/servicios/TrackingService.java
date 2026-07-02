package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TrackingService {

    /**
     * ALCANCE MEDIO 1: HISTORIAL DE AMOS
     * <p>
     * Recupera el historial de revisiones de un espíritu específico desde RavenDB y
     * devuelve la lista cronológica de todos los espíritus dominantes (amos) que lo
     * controlaron a partir de una fecha determinada.
     */
    List<Espiritu> historialDeAmos(Long espirituId, LocalDateTime desde);

    /**
     * ALCANCE MEDIO 2: EL MAPA DE CALOR TEMPORAL
     * <p>
     * Recupera el historial de revisiones de un médium específico desde RavenDB y
     * devuelve la lista cronológica de todas las coordenadas geográficas por las que
     * transitó dentro de un rango de tiempo determinado.
     */
    List<GeoJsonPoint> trayectoriaTemporalDeMedium(Long mediumId);

    /**
     * ALCANCE MEDIO 3: ANÁLISIS DE DAÑO DE BATALLA
     * <p>
     * Una consulta que busca un Médium y compare su estado actual de maná/energía
     * con su estado de hace 5 revisiones atrás.
     */
    Map<String, Object> costoDeMoverseDesdeLaUltimaUbicacion(Long mediumId);
}
