package ar.edu.unq.epersgeist.persistencia.raven;

import java.util.List;
import java.util.Optional;

public interface RavenDAO<T, ID> {
    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    boolean deleteById(ID id);

    void deleteAll();

    List<T> findRevisionsById(ID id);
}
