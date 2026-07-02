package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

public interface DemoService {

    List<Espiritu> historialDeAmos();

    List<GeoJsonPoint> demoMapaDeCalorTemporal();
//
//    void demoDanioDeBatalla() throws Exception;
//
//    void ejecutarDemosTracking() throws Exception;

    void deleteAll();
}