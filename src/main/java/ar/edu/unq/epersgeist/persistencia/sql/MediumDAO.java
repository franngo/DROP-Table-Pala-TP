package ar.edu.unq.epersgeist.persistencia.sql;

import ar.edu.unq.epersgeist.modelo.Medium;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediumDAO extends JpaRepository<Medium, Long> {

    @Query("SELECT m FROM Medium m WHERE m.id = :id AND m.deletedAt = false")
    Optional<Medium> findById(@Param("id") Long id);

    @Query("SELECT m FROM Medium m WHERE m.deletedAt = false")
    List<Medium> findAll();

    @Query("""
            SELECT m
            FROM Medium m
            JOIN m.espiritus e
            WHERE TYPE(e) = Demonio
            AND e.ubicacion.id = :santuarioId
            AND m.deletedAt = false
            AND e.deletedAt = false
            GROUP BY m
            ORDER BY COUNT(e) DESC, m.id ASC
            """)
    List<Medium> mediumConMasDemonios(
            @Param("santuarioId") Long id,
            Pageable pageable);
}




