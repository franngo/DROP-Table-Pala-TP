package ar.edu.unq.epersgeist.persistencia.mongo;

import ar.edu.unq.epersgeist.modelo.Ubicacion.UbicacionMongo;
import ar.edu.unq.epersgeist.modelo.medium.MediumMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

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
    Optional<UbicacionMongo> buscarUbicacionQueContiene(Double latitud, Double longitud);
}

