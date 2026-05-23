package ar.edu.unq.epersgeist.persistencia.neo;

import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.neo.entity.UbicacionNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface UbicacionDAONeo4j extends Neo4jRepository<UbicacionNeo4j, Long> {

    void conectar(Long idOrigen, Long idDestino, Long costo);

    Boolean estanConectadas(Long idOrigen, Long idDestino);

    List<Ubicacion> ubicacionesSobrecargadas(int umbralDeEnergia);

    List<Ubicacion> caminoMasCorto(Long idOrigen, Long idDestino);

    List<Ubicacion> caminoMasRentable(Long idOrigen, Long idDestino);
}