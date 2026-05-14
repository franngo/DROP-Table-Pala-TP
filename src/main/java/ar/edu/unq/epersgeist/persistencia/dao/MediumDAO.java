package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Medium;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediumDAO extends JpaRepository<Medium, Long> {

    @Query("""
            SELECT m
            FROM Medium m
            JOIN m.espiritus e
            WHERE TYPE(e) = Demonio
            AND e.ubicacion.id = :santuarioId
            GROUP BY m
            ORDER BY COUNT(e) DESC
            """)
    List<Medium> mediumConMasDemonios(
            @Param("santuarioId") Long id,
            Pageable pageable);
}




