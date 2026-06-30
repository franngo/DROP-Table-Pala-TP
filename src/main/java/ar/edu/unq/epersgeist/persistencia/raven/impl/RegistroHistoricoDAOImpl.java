package ar.edu.unq.epersgeist.persistencia.raven.impl;

import ar.edu.unq.epersgeist.persistencia.raven.RegistroHistoricoDAO;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.ravendb.client.documents.IDocumentStore;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.client.serverwide.DatabaseRecord;
import net.ravendb.client.serverwide.operations.CreateDatabaseOperation;
import net.ravendb.client.serverwide.operations.DeleteDatabasesOperation;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public class RegistroHistoricoDAOImpl implements RegistroHistoricoDAO {

    private final IDocumentStore documentStore;

    public RegistroHistoricoDAOImpl(IDocumentStore documentStore) {
        this.documentStore = documentStore;
    }

    // --------------------------------------------------------------------------------

    public void registrarEstado(String ravenId, Object entidad) {
        try (IDocumentSession session = documentStore.openSession()) {
            session.store(entidad, ravenId);
            session.saveChanges();
        }
    }

    // --------------------------------------------------------------------------------

    public <T> List<T> obtenerHistorial(Class<T> claseEntidad, String ravenId) {
        try (IDocumentSession session = documentStore.openSession()) {
            return session.advanced().revisions().getFor(claseEntidad, ravenId);
        }
    }

    // --------------------------------------------------------------------------------

    public <T> T obtenerEstadoEnFecha(Class<T> claseEntidad, String ravenId, Date fecha) {
        try (IDocumentSession session = documentStore.openSession()) {
            return session.advanced().revisions().get(claseEntidad, ravenId, fecha);
        }
    }

    // --------------------------------------------------------------------------------

    public List<String> obtenerTodosLosIdsDeColeccion(String nombreColeccion) {
        try (IDocumentSession session = documentStore.openSession()) {
            return session.advanced()
                    .rawQuery(ObjectNode.class, "from " + nombreColeccion)
                    .toList()
                    .stream()
                    .map(nodo -> nodo.get("@metadata").get("@id").asText())
                    .toList();
        }
    }

    // --------------------------------------------------------------------------------

    public void borrarRegistro(String ravenId) {
        try (IDocumentSession session = documentStore.openSession()) {
            session.delete(ravenId);
            session.saveChanges();
        }
    }

    // --------------------------------------------------------------------------------

    public void reiniciarBaseDeDatos() {
        String database = documentStore.getDatabase();

        documentStore.maintenance()
                .server()
                .send(new DeleteDatabasesOperation(database, true));

        documentStore.maintenance()
                .server()
                .send(new CreateDatabaseOperation(new DatabaseRecord(database)));
    }
}
