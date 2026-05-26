package ar.edu.unq.epersgeist.persistencia.neo.entity;

import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.TipoUbicacion;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node(primaryLabel = "Ubicacion")
@Getter
@Setter
@NoArgsConstructor
public class UbicacionNeo4j {

    @Id
    private Long id;

    private String nombre;

    private TipoUbicacion tipo;

    public UbicacionNeo4j(Ubicacion ubicacion) {
        this.id = ubicacion.getId();
        this.nombre = ubicacion.getNombre();
        this.tipo = ubicacion.getTipoUbicacion();
    }
}
