package ar.edu.unq.epersgeist.modelo.Ubicacion;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.CoordenadasInsuficientesException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;

import org.springframework.data.geo.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "ubicaciones")
public class UbicacionMongo {

    @Id
    private Long id;

    @GeoSpatialIndexed(
            type = GeoSpatialIndexType.GEO_2DSPHERE
    )
    private GeoJsonPolygon area;

    public UbicacionMongo(Ubicacion ubicacion, List<Coordenada> coords) {
        this.id = ubicacion.getId();
        this.area = crearPoligono(coords);
    }

    public static GeoJsonPolygon crearPoligono(List<Coordenada> coords) {
        if (coords == null || coords.size() < 3) {
            throw new CoordenadasInsuficientesException();
        }

        List<Point> puntos =
                new ArrayList<>(
                        coords.stream()
                                .map(c ->
                                        new Point(
                                                c.getLongitud(),
                                                c.getLatitud()
                                        )
                                )
                                .toList()
                );

        Coordenada primero = coords.get(0);
        Coordenada ultimo = coords.get(coords.size() - 1);

        boolean estaCerrado =
                Objects.equals(
                        primero.getLongitud(),
                        ultimo.getLongitud()
                )
                        &&
                        Objects.equals(
                                primero.getLatitud(),
                                ultimo.getLatitud()
                        );

        if (!estaCerrado) {
            puntos.add(new Point(primero.getLongitud(), primero.getLatitud()));
        }

        return new GeoJsonPolygon(puntos);
    }
}