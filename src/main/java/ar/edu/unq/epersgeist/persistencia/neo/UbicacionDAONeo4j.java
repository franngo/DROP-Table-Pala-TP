package ar.edu.unq.epersgeist.persistencia.neo;

import ar.edu.unq.epersgeist.modelo.Ubicacion.UbicacionNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionDAONeo4j extends Neo4jRepository<UbicacionNeo4j, Long> {

    // --------------------------------------------------------------------------------

    @Query("""
                MATCH (o:Ubicacion {id: $idOrigen}),
                      (d:Ubicacion {id: $idDestino})
                MERGE (o)-[r:CONECTADO_CON]->(d)
                SET r.costo = $costo
            """)
    void conectar(Long idOrigen, Long idDestino, Long costo);

    // --------------------------------------------------------------------------------

    @Query("""
                MATCH (u1:Ubicacion {id: $idOrigen}), (u2:Ubicacion {id: $idDestino})
                MATCH p=(u1)-[:CONECTADO_CON*1]->(u2)
                RETURN count(p) > 0
            """)
    Boolean estanConectadas(Long idOrigen, Long idDestino);

    // --------------------------------------------------------------------------------

    @Query("""
                MATCH (o:Ubicacion {id: $idOrigen}),
                      (d:Ubicacion {id: $idDestino})
                MATCH p = shortestPath((o)-[:CONECTADO_CON*]->(d))
                RETURN nodes(p)
            """)
    List<UbicacionNeo4j> caminoMasCorto(Long idOrigen, Long idDestino);

    // --------------------------------------------------------------------------------

    @Query("""
                MATCH (u1:Ubicacion {id: $idOrigen}), (u2:Ubicacion {id: $idDestino})
                MATCH p = (u1)-[c:CONECTADO_CON*1..]->(u2)
                RETURN nodes(p)
                ORDER BY reduce(suma = 0, r IN relationships(p) | suma + r.costo) ASC LIMIT 1
            """)
    List<UbicacionNeo4j> caminoMasRentable(Long idOrigen, Long idDestino);

    // --------------------------------------------------------------------------------

    @Query("""
                UNWIND $ids AS id
                CALL (id) {
                  MATCH (o:Ubicacion {id: id})
                  MATCH p = SHORTEST 1 ((o)-[:CONECTADO_CON*]->(d:Ubicacion))
                  WHERE d.id <> id
                  RETURN round(sum(toFloat(1) / size(tail(nodes(p)))), 3) AS centrality
                }
                RETURN id, centrality
                ORDER BY centrality DESC
            """)
    Double harmonicResult(Long id);

    // --------------------------------------------------------------------------------

    @Query("""
                MATCH (u:Ubicacion {id: $id})
                SET u.nombre = $nombre
            """)
    void actualizar(@Param("id") Long id, @Param("nombre") String nombre);

    // --------------------------------------------------------------------------------

    @Query("""
                MATCH (o:Ubicacion {id: $idOrigen})-[r:CONECTADO_CON]->(d:Ubicacion {id: $idDestino})
                RETURN r.costo
            """)
    Long costoHacia(
            @Param("idOrigen") Long idOrigen,
            @Param("idDestino") Long idDestino
    );
}
