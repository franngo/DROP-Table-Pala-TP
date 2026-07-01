package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;

import java.time.LocalDateTime;
import java.util.List;

public interface TrackingService {

    /**
     * ALCANCE MEDIO 1: LA CRISIS DE IDENTIDAD (HISTORIAL DE AMOS)
     * <p>
     * Recupera el historial de revisiones de un espíritu específico desde RavenDB y
     * devuelve la lista cronológica de todos los espíritus dominantes (amos) que lo
     * controlaron a partir de una fecha determinada.
     */
    List<Espiritu> historialDeAmos(Long espirituId, LocalDateTime desde);

    /**
     * ALCANCE MEDIO 2: EL MAPA DE CALOR TEMPORAL
     * <p>
     * Filtra la colección mediums_mongo en RavenDB, por un rango de tiempo
     * (ej. entre las 14:00 y las 15:00) y obtenés todas las coordenadas por las que caminó ese médium.
     */
//    List<GeoJsonPoint> trayectoriaTemporalDeMedium(Long mediumId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * ALCANCE MEDIO 3: ANÁLISIS DE DAÑO DE BATALLA
     * <p>
     * Una consulta que busque un Médium y compare su estado actual de maná/energía
     * con su estado de hace 5 revisiones atrás.
     */
//    Map<String, Object> analizarDanioDeBatalla(Long mediumId, int revisionesAtras);
}
