package ar.edu.unq.epersgeist.modelo.Ubicacion;

import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.TipoUbicacion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Ubicacion")
@Getter
@Setter
@NoArgsConstructor
public class UbicacionNeo4j {
    @Id
    private Long id;

    private String nombre;

    private TipoUbicacion tipo;

    public UbicacionNeo4j(Ubicacion ubicacion) {
        setId(ubicacion.getId());
        setNombre(ubicacion.getNombre());
        setTipo(ubicacion.getTipoUbicacion());
    }
}
