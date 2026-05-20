package ar.edu.unq.epersgeist.persistencia.neo.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class UbicacionNeo4j {

    @Id
    @GeneratedValue
    private Long id;

    private String nombre;

}
