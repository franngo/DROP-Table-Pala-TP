package ar.edu.unq.epersgeist.persistencia.mongo;

import ar.edu.unq.epersgeist.modelo.Espiritu.EspirituMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface EspirituDAOMongo extends MongoRepository<EspirituMongo, Long> {

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
    void actualizarCoordenada(Long id, Double longitud, Double latitud);
}
