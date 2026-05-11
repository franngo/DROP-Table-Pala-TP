package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionDAO extends JpaRepository<Ubicacion, Long> {
    @Query("SELECT COUNT(u) > 0 FROM Ubicacion u WHERE u.nombre = :nombre")
    boolean existsByName(String nombre);

    @Query("FROM Espiritu e WHERE e.ubicacion.id = :id")
    List<Espiritu> espiritusEn(Long id);

    @Query("FROM Medium m WHERE m.ubicacion.id = :id AND m.espiritus IS EMPTY")
    List<Medium> mediumsSinEspiritusEn(Long id);
}
