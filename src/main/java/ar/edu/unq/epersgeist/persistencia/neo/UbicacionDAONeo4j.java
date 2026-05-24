package ar.edu.unq.epersgeist.persistencia.neo;

import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.neo.entity.UbicacionNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UbicacionDAONeo4j extends Neo4jRepository<UbicacionNeo4j, Long> {

    //void conectar(Long idOrigen, Long idDestino, Long costo);

    @Query("""
        MATCH (u1:Ubicacion {id: $idOrigen}), (u2:Ubicacion {id: $idDestino})
        MATCH p=(u1)-[:CONECTADO_CON]-(u2)
        RETURN count(p) > 0
    """)
    Boolean estanConectadas(Long idOrigen, Long idDestino);

    // List<Ubicacion> ubicacionesSobrecargadas(int umbralDeEnergia);

    //List<Ubicacion> caminoMasCorto(Long idOrigen, Long idDestino);

    // List<Ubicacion> caminoMasRentable(Long idOrigen, Long idDestino);

    @Query("""
        MATCH (u:Ubicacion {id: $id})
        SET u.nombre = $nombre
    """)
    public void actualizar(@Param("id") Long id, @Param("nombre") String nombre);
}