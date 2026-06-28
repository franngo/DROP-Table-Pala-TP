package ar.edu.unq.epersgeist.persistencia.raven.impl;

import ar.edu.unq.epersgeist.persistencia.raven.Cuervo;
import net.ravendb.client.documents.IDocumentStore;
import org.springframework.stereotype.Repository;

@Repository
public class CuervoDaoRavenImpl extends RavenDAOImpl<Cuervo, Long> {
    public CuervoDaoRavenImpl(IDocumentStore store) {
        super(store, Cuervo.class);
    }
}