package ar.edu.unq.epersgeist.persistencia.sql;

import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspirituDAO extends JpaRepository<Espiritu, Long> {

    @Query("SELECT e FROM Espiritu e WHERE e.id = :id AND e.deletedAt = false")
    Optional<Espiritu> findById(Long id);

    @Query("SELECT e FROM Espiritu e WHERE e.deletedAt = false")
    List<Espiritu> findAll();

    @Query("SELECT e FROM Demonio e WHERE e.deletedAt = false ORDER BY e.nivelDeConexion DESC")
    List<Demonio> espiritusDemoniacos();


    @Query("SELECT e FROM Demonio e WHERE e.deletedAt = false")
    Page<Espiritu> espiritusDemoniacos(Pageable pageable);


    @Query("""
                SELECT COUNT(e)
                FROM Demonio e
                WHERE e.ubicacion.id = :santuarioId
                AND e.mediumCon IS NULL
                AND e.deletedAt = false
            """)
    Long demoniosLibres(Long santuarioId);
}