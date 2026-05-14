package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionDAO extends JpaRepository<Ubicacion, Long> {
    @Query("SELECT COUNT(u) > 0 FROM Ubicacion u WHERE u.nombre = :nombre")
    boolean existsByName(String nombre);

    @Query("SELECT e FROM Espiritu e WHERE e.ubicacion.id = :id")
    List<Espiritu> espiritusEn(Long id);

    @Query("SELECT m FROM Medium m WHERE m.ubicacion.id = :id AND m.espiritus IS EMPTY")
    List<Medium> mediumsSinEspiritusEn(Long id);


    @Query("""
                SELECT s
                FROM Santuario s, Espiritu e
                WHERE e.ubicacion = s
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
            """)
    Long cantidadDemonios(Long santuarioId);
}
