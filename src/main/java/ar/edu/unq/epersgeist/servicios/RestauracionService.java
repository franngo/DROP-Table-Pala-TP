package ar.edu.unq.epersgeist.servicios;

import java.time.LocalDateTime;

public interface RestauracionService {

    /**
     * HITO 2: VIAJE EN EL TIEMPO (Rollback Global)
     * Borra el estado actual de todas las bases de datos de la aplicación (SQL, Mongo, Neo4j)
     * y reconstruye el universo completo exactamente como estaba en la fecha especificada.
     */
    void restaurarSistemaAFechaYHora(LocalDateTime fecha);
}
