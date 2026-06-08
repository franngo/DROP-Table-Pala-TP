package ar.edu.unq.epersgeist.persistencia.mongo;

import ar.edu.unq.epersgeist.modelo.medium.MediumMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediumDAOMongo extends MongoRepository<MediumMongo, Long> {
}
