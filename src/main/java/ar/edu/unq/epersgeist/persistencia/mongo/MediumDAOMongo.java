package ar.edu.unq.epersgeist.persistencia.mongo;

import ar.edu.unq.epersgeist.modelo.medium.MediumMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.Optional;

public interface MediumDAOMongo extends MongoRepository<MediumMongo, Long> {

    @Query("""
            {
            "_id": ?0,
                    "coordenada": {
                      "$nearSphere": {
                        "$geometry": {
                            "type": "Point",
                            "coordinates": [?2, ?1]
                        },
                    "$maxDistance": ?3
                }
            }
        }
        """)
    Optional<MediumMongo> mediumDentroDeDistancia(Long mediumId, Double latitud, Double longitud, Double distanciaMaxima);

    @Query("{ '_id': ?0 }")
    @Update("""
        {
            '$set': {
                'coordenada': {
                    'type': 'Point',
                    'coordinates': [?1, ?2]
                }
            }
        }
    """)
    void actualizarCoordenada(Long mediumId, Double longitud, Double latitud);
}
