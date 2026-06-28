package ar.edu.unq.epersgeist.persistencia.raven.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import net.ravendb.client.documents.IDocumentStore;
import org.springframework.stereotype.Repository;

@Repository
public class EspirituDAORavenImpl extends RavenDAOImpl<Espiritu, Long> {
    public EspirituDAORavenImpl(IDocumentStore store) {
        super(store, Espiritu.class);
    }
}
