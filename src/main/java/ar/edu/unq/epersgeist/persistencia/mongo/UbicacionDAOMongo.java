package ar.edu.unq.epersgeist.persistencia.mongo;

import ar.edu.unq.epersgeist.modelo.Ubicacion.UbicacionMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UbicacionDAOMongo extends MongoRepository<UbicacionMongo, Long> {


}


