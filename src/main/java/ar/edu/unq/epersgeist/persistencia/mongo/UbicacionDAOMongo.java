package ar.edu.unq.epersgeist.persistencia.mongo;

import ar.edu.unq.epersgeist.modelo.Ubicacion.UbicacionMongo;
import ar.edu.unq.epersgeist.modelo.medium.MediumMongo;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UbicacionDAOMongo extends MongoRepository<UbicacionMongo, Long> {



    @Query("""
            {
            'area': {
                            $geoIntersects: {
                                $geometry: {
                                    type: 'Point',
                                    coordinates: [?1, ?0]
                                }
                            }
                        }
            }
            """)
    List<UbicacionMongo> buscarUbicacionQueContiene(Double latitud, Double longitud);

    @Query("""
            {
                area: {
                    $geoIntersects: {
                        $geometry: ?0
                    }
                }
            }
            """)
    List<UbicacionMongo> ubicacionesQueIntersectan(GeoJsonPolygon poligono);
}


