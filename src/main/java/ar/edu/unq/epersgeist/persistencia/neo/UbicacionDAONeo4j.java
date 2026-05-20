package ar.edu.unq.epersgeist.persistencia.neo;

import ar.edu.unq.epersgeist.persistencia.neo.entity.UbicacionNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UbicacionDAONeo4j extends Neo4jRepository<UbicacionNeo4j, Long> {
}