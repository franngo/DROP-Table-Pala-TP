package ar.edu.unq.epersgeist.persistencia.mongo;

import ar.edu.unq.epersgeist.modelo.Espiritu.EspirituMongo;
import ar.edu.unq.epersgeist.modelo.ResultadoDistancia;
import org.springframework.data.mongodb.repository.Aggregation;
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

    @Aggregation(pipeline = {
            "{ $geoNear: { " +
                    "near: { type: 'Point', coordinates: [?0, ?1] }, " +
                    "key: 'coordenada', " +
                    "distanceField: 'distanciaCalculada', " +
                    "spherical: true, " +
                    "query: { _id: ?2 } " +
                    "} " +
            "}",
            "{ $project: { '_id': 0, 'distanciaCalculada': 1}}"
    })
    public ResultadoDistancia distanciaHastaEspiritu(double longitudDominante, double latitudDominante, Long espirituADominarId);

}
