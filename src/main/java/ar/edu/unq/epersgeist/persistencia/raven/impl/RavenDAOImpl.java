package ar.edu.unq.epersgeist.persistencia.raven.impl;

import ar.edu.unq.epersgeist.persistencia.raven.RavenDAO;
import net.ravendb.client.documents.IDocumentStore;
import net.ravendb.client.documents.session.IDocumentSession;

import java.util.List;
import java.util.Optional;

public class RavenDAOImpl<T, ID> implements RavenDAO<T, ID> {
    protected final IDocumentStore store;
    private final Class<T> clazz;

    public RavenDAOImpl(IDocumentStore store, Class<T> clazz) {
        this.store = store;
        this.clazz = clazz;
    }

    // --------------------------------------------------------------------------------

    @Override
    public T save(T entity) {
        try (IDocumentSession session = store.openSession()) {
            session.store(entity);
            session.saveChanges();
            return entity;
        }
    }

    // --------------------------------------------------------------------------------

    @Override
    public Optional<T> findById(ID id) {
        try (IDocumentSession session = store.openSession()) {
            T entity = session.load(clazz, String.valueOf(id));
            return Optional.ofNullable(entity);
        }
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<T> findAll() {
        try (IDocumentSession session = store.openSession()) {
            return session.query(clazz).toList();
        }
    }

    // --------------------------------------------------------------------------------

    @Override
    public boolean deleteById(ID id) {
        try (IDocumentSession session = store.openSession()) {
            String stringId = String.valueOf(id);
            if (session.advanced().exists(stringId)) {
                session.delete(stringId);
                session.saveChanges();
                return true;
            }
            return false;
        }
    }

    // --------------------------------------------------------------------------------

    @Override
    public void deleteAll() {
        try (IDocumentSession session = store.openSession()) {
            List<T> all = session.query(clazz).toList();
            for (T entity : all) {
                session.delete(entity);
            }
            session.saveChanges();
        }
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<T> findRevisionsById(ID id) {
        try (IDocumentSession session = store.openSession()) {
            return session.advanced().revisions().getFor(clazz, String.valueOf(id));
        }
    }
}
