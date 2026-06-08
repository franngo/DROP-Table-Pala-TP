package ar.edu.unq.epersgeist.modelo.Espiritu;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "espiritus")
public class EspirituMongo {

    @Id
    private Long id;

    @GeoSpatialIndexed(
            type = GeoSpatialIndexType.GEO_2DSPHERE
    )
    private GeoJsonPoint coordenada;

    public EspirituMongo(Espiritu espiritu, Coordenada coordenada) {

        this.id = espiritu.getId();

        this.coordenada =
                new GeoJsonPoint(
                        coordenada.getLongitud(),
                        coordenada.getLatitud()
                );
    }
}
