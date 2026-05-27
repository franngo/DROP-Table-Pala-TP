package ar.edu.unq.epersgeist.persistencia.sql;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UbicacionDAOSQL extends JpaRepository<Ubicacion, Long> {

    @Query("SELECT u FROM Ubicacion u WHERE u.id = :id AND u.deletedAt = false")
    Optional<Ubicacion> findById(Long id);

    @Query("SELECT u FROM Ubicacion u WHERE u.deletedAt = false")
    List<Ubicacion> findAll();


    @Query("SELECT COUNT(u) > 0 FROM Ubicacion u WHERE u.nombre = :nombre AND u.deletedAt = false")
    boolean existsByName(String nombre);

    @Query("SELECT e FROM Espiritu e WHERE e.ubicacion.id = :id")
    List<Espiritu> espiritusEn(Long id);

    @Query("SELECT m FROM Medium m WHERE m.ubicacion.id = :id AND m.espiritus IS EMPTY")
    List<Medium> mediumsSinEspiritusEn(Long id);


    @Query("""
                SELECT s
                FROM Santuario s, Espiritu e
                WHERE e.ubicacion = s
                AND s.deletedAt = false
                AND e.deletedAt = false
                GROUP BY s
                ORDER BY
                    SUM(
                        CASE
                            WHEN TYPE(e) = Demonio THEN 1
                            WHEN TYPE(e) = Angelical THEN -1
                            ELSE 0
                        END
                    ) DESC
            """)
    List<Santuario> santuariosMasCorruptos(Pageable pageable);

    @Query("""
                SELECT COUNT(e)
                FROM Demonio e
                WHERE e.ubicacion.id = :santuarioId
                AND e.deletedAt = false
            """)
    Long cantidadDemonios(Long santuarioId);

    @Query ("""
                SELECT u
                FROM Ubicacion u
                WHERE u.energia > :umbralDeEnergia
                AND u.deletedAt = false
            """)
    List<Ubicacion> ubicacionSobrecargada(int umbralDeEnergia);
}
