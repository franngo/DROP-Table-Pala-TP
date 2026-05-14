package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspirituDAO extends JpaRepository<Espiritu, Long> {

    @Query("SELECT e FROM Demonio e ORDER BY e.nivelDeConexion DESC")
    List<Demonio> espiritusDemoniacos();


    @Query("SELECT e FROM Demonio e")
    Page<Espiritu> espiritusDemoniacos(Pageable pageable);


    @Query("""
                SELECT COUNT(e)
                FROM Demonio e
                WHERE e.ubicacion.id = :santuarioId
                AND e.mediumCon IS NULL
            """)
    Long demoniosLibres(Long santuarioId);
}