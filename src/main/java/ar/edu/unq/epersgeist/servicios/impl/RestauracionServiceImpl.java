package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Espiritu.EspirituMongo;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.Ubicacion.UbicacionMongo;
import ar.edu.unq.epersgeist.modelo.Ubicacion.UbicacionNeo4j;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.medium.MediumMongo;
import ar.edu.unq.epersgeist.persistencia.mongo.EspirituDAOMongo;
import ar.edu.unq.epersgeist.persistencia.mongo.MediumDAOMongo;
import ar.edu.unq.epersgeist.persistencia.mongo.UbicacionDAOMongo;
import ar.edu.unq.epersgeist.persistencia.neo.UbicacionDAONeo4j;
import ar.edu.unq.epersgeist.persistencia.sql.EspirituDAOSQL;
import ar.edu.unq.epersgeist.persistencia.sql.MediumDAOSQL;
import ar.edu.unq.epersgeist.persistencia.sql.UbicacionDAOSQL;
import ar.edu.unq.epersgeist.servicios.RegistroHistoricoService;
import ar.edu.unq.epersgeist.servicios.RestauracionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RestauracionServiceImpl implements RestauracionService {

    private final RegistroHistoricoService registroHistoricoService;

    // DAOs Espiritu
    private final EspirituDAOSQL espirituDAOSQL;
    private final EspirituDAOMongo espirituDAOMongo;

    // DAOs Medium
    private final MediumDAOSQL mediumDAOSQL;
    private final MediumDAOMongo mediumDAOMongo;

    // DAOs Ubicacion
    private final UbicacionDAOSQL ubicacionDAOSQL;
    private final UbicacionDAOMongo ubicacionDAOMongo;
    private final UbicacionDAONeo4j ubicacionDAONeo4j;

    public RestauracionServiceImpl(
            RegistroHistoricoService registroHistoricoService,

            EspirituDAOSQL espirituDAOSQL,
            EspirituDAOMongo espirituDAOMongo,

            MediumDAOSQL mediumDAOSQL,
            MediumDAOMongo mediumDAOMongo,

            UbicacionDAOSQL ubicacionDAOSQL,
            UbicacionDAOMongo ubicacionDAOMongo,
            UbicacionDAONeo4j ubicacionDAONeo4j
    ) {
        this.registroHistoricoService = registroHistoricoService;

        this.espirituDAOSQL = espirituDAOSQL;
        this.espirituDAOMongo = espirituDAOMongo;

        this.mediumDAOSQL = mediumDAOSQL;
        this.mediumDAOMongo = mediumDAOMongo;

        this.ubicacionDAOSQL = ubicacionDAOSQL;
        this.ubicacionDAOMongo = ubicacionDAOMongo;
        this.ubicacionDAONeo4j = ubicacionDAONeo4j;
    }

    // --------------------------------------------------------------------------------

    @Override
    @Transactional // Si algo falla reinsertando en SQL o Mongo, la transacción hace rollback y el presente NO SE PIERDE
    public void restaurarSistemaAFechaYHora(LocalDateTime fecha) {

        // ============================================================================
        // PASO 1: LEER TODOS LOS PLANOS DEL PASADO (Desde la caja negra en RavenDB)
        // ============================================================================

        List<String> idsUbicacionesSQL = new ArrayList<String>(registroHistoricoService.obtenerTodosLosIdsDeColeccion("Santuarios"));
        List<String> idsUbicacionesSQL2 = registroHistoricoService.obtenerTodosLosIdsDeColeccion("Cementerios");
        idsUbicacionesSQL.addAll(idsUbicacionesSQL2);
        List<Ubicacion> ubicacionesSQL = recuperarFotosDelPasado(Ubicacion.class, idsUbicacionesSQL, fecha);

//        List<String> idsUbicacionesMongo = registroHistoricoService.obtenerTodosLosIdsDeColeccion("UbicacionMongos");
//        List<UbicacionMongo> ubicacionesMongo = recuperarFotosDelPasado(UbicacionMongo.class, idsUbicacionesMongo, fecha);
//
//        List<String> idsUbicacionesNeo = registroHistoricoService.obtenerTodosLosIdsDeColeccion("UbicacionNeo4js");
//        List<UbicacionNeo4j> ubicacionesNeo = recuperarFotosDelPasado(UbicacionNeo4j.class, idsUbicacionesNeo, fecha);

        // ----------------------------------------

        List<String> idsMediumsSQL = registroHistoricoService.obtenerTodosLosIdsDeColeccion("Mediums");
        List<Medium> mediumsSQL = recuperarFotosDelPasado(Medium.class, idsMediumsSQL, fecha);

//        List<String> idsMediumsMongo = registroHistoricoService.obtenerTodosLosIdsDeColeccion("MediumMongos");
//        List<MediumMongo> mediumsMongo = recuperarFotosDelPasado(MediumMongo.class, idsMediumsMongo, fecha);

        // ----------------------------------------

        List<String> idsEspiritusSQL = new ArrayList<String>(registroHistoricoService.obtenerTodosLosIdsDeColeccion("Angelicals"));
        List<String> idsEspiritusSQL2 = registroHistoricoService.obtenerTodosLosIdsDeColeccion("Demonios");
        idsEspiritusSQL.addAll(idsEspiritusSQL2);
        List<Espiritu> espiritusSQL = recuperarFotosDelPasado(Espiritu.class, idsEspiritusSQL, fecha);

//        List<String> idsEspiritusMongo = registroHistoricoService.obtenerTodosLosIdsDeColeccion("EspirituMongos");
//        List<EspirituMongo> espiritusMongo = recuperarFotosDelPasado(EspirituMongo.class, idsEspiritusMongo, fecha);

        // ============================================================================
        // PASO 2: LIMPIEZA ABSOLUTA DEL PRESENTE (En orden inverso de FKs para evitar caídas)
        // ============================================================================

        // A. Primero Espíritus
//        espirituDAOMongo.deleteAll();
        espirituDAOSQL.deleteAll();
        System.out.println(espirituDAOSQL.count());

        // B. Luego eliminamos los Médiums
//        mediumDAOMongo.deleteAll();
        mediumDAOSQL.deleteAll();

        // C. Por último las Ubicaciones
//        ubicacionDAOMongo.deleteAll();
//        ubicacionDAONeo4j.deleteAll();
        ubicacionDAOSQL.deleteAll();
        System.out.println(ubicacionDAOSQL.count());


        // ============================================================================
        // PASO 3: RECONSTRUCCIÓN CRONOLÓGICA (Siguiendo las restricciones de integridad)
        // ============================================================================

        // ----------------------------------------
        // A. Restauramos Ubicaciones
        // ----------------------------------------

        ubicacionDAOSQL.saveAll(ubicacionesSQL);
        ubicacionDAOSQL.findAll().forEach(u ->
                System.out.println(u.getId() + " - " + u.getNombre())
        );
        espirituDAOSQL.flush();

//        for (UbicacionNeo4j u : ubicacionesNeo) {
//            ubicacionDAONeo4j.save(u);
//        }
//
//        for (UbicacionMongo u : ubicacionesMongo) {
//            ubicacionDAOMongo.save(u);
//        }

        // ----------------------------------------
        // B. Restauramos los Médiums
        // ----------------------------------------

        mediumDAOSQL.saveAll(mediumsSQL);
        mediumDAOSQL.flush();

//        for (MediumMongo m : mediumsMongo) {
//            mediumDAOMongo.save(m);
//        }

        // ----------------------------------------
        // C. Restauramos los Espíritus
        // ----------------------------------------

        espirituDAOSQL.saveAll(espiritusSQL);
        espirituDAOSQL.flush();

//        for (EspirituMongo e : espiritusMongo) {
//            espirituDAOMongo.save(e);
//        }
    }

    // --------------------------------------------------------------------------------

    /**
     * Método helper privado para iterar los IDs de RavenDB y traer las fotos de una fecha concreta.
     * Filtra los nulos en caso de que un objeto no existiera todavía en esa fecha del pasado.
     */
    private <T> List<T> recuperarFotosDelPasado(Class<T> clase, List<String> ravenIds, LocalDateTime fecha) {
        return ravenIds.stream()
                .map(id -> registroHistoricoService.obtenerEstadoEnFecha(clase, id, fecha))
                .filter(Objects::nonNull)
                .toList();
    }
}
