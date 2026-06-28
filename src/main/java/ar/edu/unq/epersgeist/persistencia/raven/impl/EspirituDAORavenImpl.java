package ar.edu.unq.epersgeist.persistencia.raven.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.EspirituRaven;
import net.ravendb.client.documents.IDocumentStore;
import org.springframework.stereotype.Repository;

@Repository
public class EspirituDAORavenImpl extends RavenDAOImpl<EspirituRaven, String> {
    public EspirituDAORavenImpl(IDocumentStore store) {
        super(store, EspirituRaven.class);
    }
}
