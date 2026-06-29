package ar.edu.unq.epersgeist.persistencia.raven.impl;

import ar.edu.unq.epersgeist.persistencia.raven.RegistroHistoricoDAO;
import net.ravendb.client.documents.IDocumentStore;
import net.ravendb.client.documents.session.IDocumentSession;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class RegistroHistoricoDAOImpl implements RegistroHistoricoDAO {

    private final IDocumentStore documentStore;

    // Se inyecta el IDocumentStore que configuraste globalmente en Spring
    public RegistroHistoricoDAOImpl(IDocumentStore documentStore) {
        this.documentStore = documentStore;
    }

    /**
     * C (CREATE / UPDATE) de la historia.
     * Guarda el estado actual de cualquier entidad. Si el Revisioning está activo en RavenDB,
     * este método generará automáticamente una nueva revisión (un hito en la historia).
     */
    public void registrarEstado(String ravenId, Object entidad) {
        try (IDocumentSession session = documentStore.openSession()) {
            session.store(entidad, ravenId);
            session.saveChanges();
        }
    }

    /**
     * R (READ) - HITO 1: HISTORIAL CRONOLÓGICO
     * Devuelve todas las revisiones del pasado de una entidad ordenadas cronológicamente.
     */
    public <T> List<T> obtenerHistorial(Class<T> claseEntidad, String ravenId) {
        try (IDocumentSession session = documentStore.openSession()) {
            // El método nativo de RavenDB .getFor() te trae el historial inmutable
            return session.advanced().revisions().getFor(claseEntidad, ravenId);
        }
    }

    /**
     * R (READ) - HITO 2: VIAJE EN EL TIEMPO (BONUS 1)
     * Devuelve cómo era la entidad exactamente en una fecha determinada.
     */
    public <T> T obtenerEstadoEnFecha(Class<T> claseEntidad, String ravenId, Date fecha) {
        try (IDocumentSession session = documentStore.openSession()) {
            // Busca en la línea de tiempo el snapshot inmutable de esa fecha exacta
            return session.advanced().revisions().get(claseEntidad, ravenId, fecha);
        }
    }

    /**
     * D (DELETE) - PARA TESTS AUTOMÁTICOS
     * Elimina el documento actual (y por ende sus revisiones) para limpiar la base entre tests.
     */
    public void eliminarRegistroDePrueba(String ravenId) {
        try (IDocumentSession session = documentStore.openSession()) {
            session.delete(ravenId);
            session.saveChanges();
        }
    }
}
