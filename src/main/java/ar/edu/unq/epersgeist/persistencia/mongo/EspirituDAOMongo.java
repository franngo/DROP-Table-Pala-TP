package ar.edu.unq.epersgeist.persistencia.mongo;

import ar.edu.unq.epersgeist.modelo.Espiritu.EspirituMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EspirituDAOMongo extends MongoRepository<EspirituMongo, Long> {
}
