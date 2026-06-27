package ar.edu.unq.epersgeist.persistencia.raven.cuervo.impl;

import ar.edu.unq.epersgeist.persistencia.raven.cuervo.Cuervo;
import ar.edu.unq.epersgeist.persistencia.raven.cuervo.CuervoDaoRaven;
import net.ravendb.client.documents.IDocumentStore;


public class CuervoDAO implements CuervoDaoRaven {

    private final IDocumentStore store;

    public CuervoDAO(IDocumentStore store) {
        this.store = store;
    }

    @Override
    public Cuervo crear(String nombre) {
        return null;
    }
//
//    @Override
//    public Cuervo crear(String nombre) {
//        try (IDocumentSession session = store.openSession()) {
//            session.store(usuario);
//            session.saveChanges();
//            return usuario.getId();
//        }
//    }
}
