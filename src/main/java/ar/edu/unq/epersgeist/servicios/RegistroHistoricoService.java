package ar.edu.unq.epersgeist.servicios;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroHistoricoService {

    /**
     * Guarda el estado actual de una entidad en un documento específico de RavenDB.
     */
    void registrarEstado(String ravenId, Object entidad);

    /**
     * Devuelve la lista completa de cambios (versiones pasadas) de una entidad.
     */
    <T> List<T> obtenerHistorial(Class<T> claseEntidad, String ravenId);

    /**
     * Recupera el estado exacto en el que se encontraba una entidad en una fecha determinada.
     */
    <T> T obtenerEstadoEnFecha(Class<T> claseEntidad, String ravenId, LocalDateTime fecha);

    /**
     * Limpia por completo la base de datos de RavenDB (Exclusivo para entornos de Test).
     */
    void reiniciarBaseDeDatos();

    /**
     * Obtiene todos los identificadores de documentos almacenados en una colección dada.
     */
    List<String> obtenerTodosLosIdsDeColeccion(String nombreColeccion);

}
