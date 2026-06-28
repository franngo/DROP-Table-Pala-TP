package ar.edu.unq.epersgeist.persistencia.raven.impl;

import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import net.ravendb.client.documents.IDocumentStore;
import org.springframework.stereotype.Repository;

@Repository
public class UbicacionDAORavenImpl extends RavenDAOImpl<Ubicacion, Long> {
    public UbicacionDAORavenImpl(IDocumentStore store) {
        super(store, Ubicacion.class);
    }
}
