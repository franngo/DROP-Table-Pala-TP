package ar.edu.unq.epersgeist.persistencia.raven;

import java.util.Date;
import java.util.List;

public interface RegistroHistoricoDAO {

    /**
     * C (CREATE / UPDATE) de la historia.
     * Guarda el estado actual de cualquier entidad. Si el Revisioning está activo en RavenDB,
     * este método generará automáticamente una nueva revisión (un hito en la historia).
     */
    void registrarEstado(String ravenId, Object entidad);

    // --------------------------------------------------------------------------------

    /**
     * R (READ) - HITO 1: HISTORIAL CRONOLÓGICO
     * Devuelve todas las revisiones del pasado de una entidad ordenadas cronológicamente.
     */
    <T> List<T> obtenerHistorial(Class<T> claseEntidad, String ravenId);

    // --------------------------------------------------------------------------------

    /**
     * R (READ) - HITO 2: VIAJE EN EL TIEMPO (BONUS 1)
     * Devuelve cómo era la entidad exactamente en una fecha determinada.
     */
    <T> T obtenerEstadoEnFecha(Class<T> claseEntidad, String ravenId, Date fecha);

    // --------------------------------------------------------------------------------

    /**
     * R (READ) - Útil para el Rollback Global.
     * Devuelve todos los IDs reales guardados en una colección específica de RavenDB
     * (ej: "espiritus_sql", "espiritus_mongo").
     */
    List<String> obtenerTodosLosIdsDeColeccion(String nombreColeccion);

    // --------------------------------------------------------------------------------

    /**
     * D (DELETE) - PARA TESTS AUTOMÁTICOS
     * Elimina el documento actual (y por ende sus revisiones) para limpiar la base entre tests.
     */
    void borrarRegistro(String ravenId);

    // --------------------------------------------------------------------------------

    /**
     * D (DELETE) - PARA TESTS AUTOMÁTICOS
     * Vacía por completo la base de datos de RavenDB borrando cada documento existente.
     * Útil para garantizar que cada test de JUnit corra en un entorno limpio e independiente.
     */
    void reiniciarBaseDeDatos();
}
