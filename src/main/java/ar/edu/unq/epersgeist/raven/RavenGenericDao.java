package ar.edu.unq.epersgeist.raven;

import java.util.List;
import java.util.Optional;

public interface RavenGenericDao<T, ID> {
    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    boolean deleteById(ID id);

    void deleteAll();
}
