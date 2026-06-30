package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.servicios.RestauracionService;
import org.springframework.stereotype.Service;

@Service
public class RestauracionServiceImpl implements RestauracionService {

//    private final RegistroHistoricoService registroHistoricoService;
//
//    // DAOs de la base de datos relacional (Presente SQL)
//    private final EspirituDAOSQL espirituDAOSQL;
//    private final MediumDAOSQL mediumDAOSQL;
//    private final UbicacionDAOSQL ubicacionDAOSQL;
//
//    // DAOs de la base de datos documental (Presente MongoDB)
//    private final EspirituDAOMongo espirituDAOMongo;
//    private final UbicacionDAOMongo ubicacionDAOMongo;
//
//    public RestauracionServiceImpl(RegistroHistoricoService registroHistoricoService,
//                                   EspirituDAOSQL espirituDAOSQL, MediumDAOSQL mediumDAOSQL, UbicacionDAOSQL ubicacionDAOSQL,
//                                   EspirituDAOMongo espirituDAOMongo, UbicacionDAOMongo ubicacionDAOMongo) {
//        this.registroHistoricoService = registroHistoricoService;
//        this.espirituDAOSQL = espirituDAOSQL;
//        this.mediumDAOSQL = mediumDAOSQL;
//        this.ubicacionDAOSQL = ubicacionDAOSQL;
//        this.espirituDAOMongo = espirituDAOMongo;
//        this.ubicacionDAOMongo = ubicacionDAOMongo;
//    }
//
//    // --------------------------------------------------------------------------------
//
//    @Override
//    @Transactional // Si algo falla reinsertando en SQL o Mongo, la transacción hace rollback y el presente NO SE PIERDE
//    public void restaurarSistemaAFecha(LocalDateTime fecha) {
//
//        // ============================================================================
//        // PASO 1: LEER TODOS LOS PLANOS DEL PASADO (Desde la caja negra en RavenDB)
//        // ============================================================================
//
//        // Buscamos las fotos de las Ubicaciones
//        List<String> idsUbicacionesSQL = registroHistoricoService.obtenerTodosLosIdsDeColeccion("ubicaciones_sql");
//        List<Ubicacion> ubicacionesPasadas = recuperarFotosDelPasado(Ubicacion.class, idsUbicacionesSQL, fecha);
//
//        List<String> idsUbicacionesMongo = registroHistoricoService.obtenerTodosLosIdsDeColeccion("ubicaciones_mongo");
//        List<UbicacionMongo> ubicacionesMongoPasadas = recuperarFotosDelPasado(UbicacionMongo.class, idsUbicacionesMongo, fecha);
//
//        // Buscamos las fotos de los Médiums
//        List<String> idsMediumsSQL = registroHistoricoService.obtenerTodosLosIdsDeColeccion("mediums_sql");
//        List<Medium> mediumsPasados = recuperarFotosDelPasado(Medium.class, idsMediumsSQL, fecha);
//
//        // Buscamos las fotos de los Espíritus
//        List<String> idsEspiritusSQL = registroHistoricoService.obtenerTodosLosIdsDeColeccion("espiritus_sql");
//        List<Espiritu> espiritusPasados = recuperarFotosDelPasado(Espiritu.class, idsEspiritusSQL, fecha);
//
//        List<String> idsEspiritusMongo = registroHistoricoService.obtenerTodosLosIdsDeColeccion("espiritus_mongo");
//        List<EspirituMongo> espiritusMongoPasadas = recuperarFotosDelPasado(EspirituMongo.class, idsEspiritusMongo, fecha);
//
//
//        // ============================================================================
//        // PASO 2: LIMPIEZA ABSOLUTA DEL PRESENTE (En orden inverso de FKs para evitar caídas)
//        // ============================================================================
//
//        // A. Primero eliminamos las hojas de la dependencia (Espíritus)
//        espirituDAOMongo.deleteAll();
//        espirituDAOSQL.deleteAll();
//
//        // B. Luego eliminamos los Médiums
//        mediumDAOSQL.deleteAll();
//
//        // C. Por último las raíces estructurales (Ubicaciones)
//        ubicacionDAOMongo.deleteAll();
//        // Si usás Neo4j para las conexiones de ubicaciones, recordá vaciarlo acá: ubicacionDAONeo4j.deleteAll();
//        ubicacionDAOSQL.deleteAll();
//
//
//        // ============================================================================
//        // PASO 3: RECONSTRUCCIÓN CRONOLÓGICA (Siguiendo las restricciones de integridad)
//        // ============================================================================
//
//        // A. Restauramos las Estructuras (Ubicaciones)
//        for (Ubicacion ubi : ubicacionesPasadas) {
//            ubicacionDAOSQL.restaurarConIdNativo(ubi); // Método tuyo customizado para forzar el ID histórico
//        }
//        for (UbicacionMongo ubiMongo : ubicacionesMongoPasadas) {
//            ubicacionDAOMongo.save(ubiMongo);
//        }
//
//        // B. Restauramos los Contenedores (Médiums)
//        for (Medium med : mediumsPasados) {
//            mediumDAOSQL.restaurarConIdNativo(med);
//        }
//
//        // C. Restauramos las Entidades dependientes (Espíritus)
//        for (Espiritu esp : espiritusPasados) {
//            espirituDAOSQL.restaurarConIdNativo(esp);
//        }
//        for (EspirituMongo espMongo : espiritusMongoPasadas) {
//            espirituDAOMongo.save(espMongo);
//        }
//    }
//
//    // --------------------------------------------------------------------------------
//
//    /**
//     * Método helper privado para iterar los IDs de RavenDB y traer las fotos de una fecha concreta.
//     * Filtra los nulos en caso de que un objeto no existiera todavía en esa fecha del pasado.
//     */
//    private <T> List<T> recuperarFotosDelPasado(Class<T> clase, List<String> ravenIds, LocalDateTime fecha) {
//        return ravenIds.stream()
//                .map(id -> registroHistoricoService.obtainEstadoEnFecha(clase, id, fecha))
//                .filter(Objects::nonNull)
//                .toList();
//    }
}
