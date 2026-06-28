package ar.edu.unq.epersgeist.persistencia.raven.impl;

import ar.edu.unq.epersgeist.modelo.medium.Medium;
import net.ravendb.client.documents.IDocumentStore;
import org.springframework.stereotype.Repository;

@Repository
public class MediumDAORavenImpl extends RavenDAOImpl<Medium, Long> {
    public MediumDAORavenImpl(IDocumentStore store) {
        super(store, Medium.class);
    }
}
