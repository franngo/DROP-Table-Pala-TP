package ar.edu.unq.epersgeist.servicios;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TrackingService {

    /**
     * ALCANCE MEDIO 1: LA CRISIS DE IDENTIDAD
     * <p>
     * Trae el historial completo de revisiones de un espíritu específico y analiza cuántas veces cambió de bando
     * (de estar libre a dominado, o viceversa) desde una fecha específica.
     */
    int cantidadDeCambiosDeBando(Long espirituId, LocalDateTime desde);

    /**
     * ALCANCE MEDIO 2: EL MAPA DE CALOR TEMPORAL
     * <p>
     * Filtra la colección mediums_mongo en RavenDB, por un rango de tiempo
     * (ej. entre las 14:00 y las 15:00) y obtenés todas las coordenadas por las que caminó ese médium.
     */
    List<GeoJsonPoint> trayectoriaTemporalDeMedium(Long mediumId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * ALCANCE MEDIO 3: ANÁLISIS DE DAÑO DE BATALLA
     * <p>
     * Una consulta que busque un Médium y compare su estado actual de maná/energía
     * con su estado de hace 5 revisiones atrás.
     */
    Map<String, Object> analizarDanioDeBatalla(Long mediumId, int revisionesAtras);
}
