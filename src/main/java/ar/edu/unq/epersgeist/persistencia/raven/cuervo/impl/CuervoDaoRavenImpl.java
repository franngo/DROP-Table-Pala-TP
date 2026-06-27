package ar.edu.unq.epersgeist.persistencia.raven.cuervo.impl;

import ar.edu.unq.epersgeist.persistencia.raven.cuervo.Cuervo;
import ar.edu.unq.epersgeist.persistencia.raven.cuervo.CuervoDaoRaven;
import net.ravendb.client.documents.IDocumentStore;
import net.ravendb.client.documents.session.IDocumentSession;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CuervoDaoRavenImpl implements CuervoDaoRaven {

    private final IDocumentStore store;

    public CuervoDaoRavenImpl(IDocumentStore store) {
        this.store = store;
    }

    @Override
    public Cuervo guardar(Cuervo cuervo) {
        try (IDocumentSession session = store.openSession()) {
            session.store(cuervo);
            session.saveChanges();
            return cuervo;
        }
    }

    @Override
    public Cuervo actualizar(Cuervo cuervo) {
        try (IDocumentSession session = store.openSession()) {

            if (cuervo.getId() == null) {
                throw new IllegalArgumentException("No se puede actualizar un Cuervo sin id");
            }

            session.store(cuervo);
            session.saveChanges();
            return cuervo;
        }
    }

    @Override
    public Cuervo buscarPorId(String id) {
        try (IDocumentSession session = store.openSession()) {
            return session.load(Cuervo.class, id);
        }
    }

    @Override
    public List<Cuervo> recuperarTodos() {
        try (IDocumentSession session = store.openSession()) {
            return session.query(Cuervo.class)
                    .toList();
        }
    }

    @Override
    public void eliminar(String id) {
        try (IDocumentSession session = store.openSession()) {
            Cuervo cuervo = session.load(Cuervo.class, id);

            if (cuervo != null) {
                session.delete(cuervo);
                session.saveChanges();
            }
        }
    }

    @Override
    public void eliminarTodos() {
        try (IDocumentSession session = store.openSession()) {

            List<Cuervo> todos = session.query(Cuervo.class).toList();

            for (Cuervo c : todos) {
                session.delete(c);
            }

            session.saveChanges();
        }
    }
}