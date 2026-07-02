//package ar.edu.unq.epersgeist.servicios.impl;
//
//import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
//import ar.edu.unq.epersgeist.modelo.medium.Medium;
//import ar.edu.unq.epersgeist.modelo.medium.MediumMongo;
//import ar.edu.unq.epersgeist.persistencia.raven.RegistroHistoricoDAO;
//import ar.edu.unq.epersgeist.servicios.TrackingService;
//import net.ravendb.client.documents.IDocumentStore;
//import net.ravendb.client.documents.session.IDocumentSession;
//import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.time.OffsetDateTime;
//import java.util.*;
//
//@Service
//public class TrackingServiceImpl implements TrackingService {
//
//    private final RegistroHistoricoDAO registroHistoricoDAO;
//    private final IDocumentStore documentStore;
//
//    public TrackingServiceImpl(RegistroHistoricoDAO registroHistoricoDAO, IDocumentStore documentStore) {
//        this.registroHistoricoDAO = registroHistoricoDAO;
//        this.documentStore = documentStore;
//    }
//
//    // --------------------------------------------------------------------------------
//    // ALCANCE MEDIO 1. LA CRISIS DE IDENTIDAD
//    // --------------------------------------------------------------------------------
//
//    @Override
//    public int cantidadDeCambiosDeBando(Long espirituId, LocalDateTime desde) {
//        String idRaven = "espiritus_sql/" + espirituId;
//        int cambios = 0;
//
//        List<Espiritu> revisiones = registroHistoricoDAO.obtenerHistorial(Espiritu.class, idRaven);
//
//        Boolean ultimoEstadoDominado = null;
//
//        try (IDocumentSession session = documentStore.openSession()) {

//            for (Espiritu rev : revisiones) {
//
//
//                // Extraemos la fecha de modificación usando @last-modified
//                String fechaStr = session.advanced().getMetadataFor(rev).getString("@last-modified");
//                LocalDateTime fechaRev = OffsetDateTime.parse(fechaStr).toLocalDateTime();
//
//                if (fechaRev.isAfter(desde)) {
//                    boolean estaDominadoAhora = rev.estaDominado();
//
//                    if (ultimoEstadoDominado != null && ultimoEstadoDominado != estaDominadoAhora) {
//                        cambios++;
//                    }
//                    ultimoEstadoDominado = estaDominadoAhora;
//                }
//            }
//        }
//        return cambios;
//    }
//
/// /    @Override
/// /    public int cantidadDeCambiosDeBando(Long espirituId, LocalDateTime desde) {
/// /        String idRaven = "espiritus_sql/" + espirituId;
/// /        int cambios = 0;
/// /
/// /        try (IDocumentSession session = documentStore.openSession()) {
/// /            List<IMetadataDictionary> metadatas = session.advanced().revisions().getMetadataFor(idRaven);
/// /
/// /            if (metadatas.isEmpty()) {
/// /                return 0;
/// /            }
/// /
/// /            // Damos vuelta para procesar cronológicamente (Pasado -> Presente)
/// /            List<IMetadataDictionary> cronologico = new ArrayList<>(metadatas);
/// /            Collections.reverse(cronologico);
/// /
/// /            Boolean ultimoEstadoDominado = null;
/// /
/// /            for (IMetadataDictionary meta : cronologico) {
/// /                // El diccionario de metadatos de Raven implementa métodos tipados como getString()
/// /                // Raven usa las constantes de metadatos del sistema, "Last-Modified" y "@change-vector"
/// /                String fechaStr = meta.getString("Last-Modified");
/// /                LocalDateTime fechaRev = OffsetDateTime.parse(fechaStr).toLocalDateTime();
/// /
/// /                String changeVector = meta.getString("@change-vector");
/// /                Espiritu rev = session.advanced().revisions().get(Espiritu.class, changeVector);
/// /
/// /                if (fechaRev.isAfter(desde)) {
/// /                    boolean estaDominadoAhora = rev.estaDominado();
/// /
/// /                    if (ultimoEstadoDominado != null && ultimoEstadoDominado != estaDominadoAhora) {
/// /                        cambios++;
/// /                    }
/// /                    ultimoEstadoDominado = estaDominadoAhora;
/// /                } else {
/// /                    // Mantener el hilo del bando en el que venía antes de entrar al rango "desde"
/// /                    ultimoEstadoDominado = rev.estaDominado();
/// /                }
/// /            }
/// /        }
/// /
/// /        return cambios;
/// /    }
//
//
//    // --------------------------------------------------------------------------------
//    // ALCANCE MEDIO 2. EL MAPA DE CALOR TEMPORAL
//    // --------------------------------------------------------------------------------
//    public List<GeoJsonPoint> trayectoriaTemporalDeMedium(Long mediumId, LocalDateTime inicio, LocalDateTime fin) {
//        String idRaven = "mediums_mongo/" + mediumId;
//        List<GeoJsonPoint> trayectoria = new ArrayList<>();
//
//        List<MediumMongo> revisiones = registroHistoricoDAO.obtenerHistorial(MediumMongo.class, idRaven);
//
//        try (IDocumentSession session = documentStore.openSession()) {
//            for (MediumMongo rev : revisiones) {
//                // Extraemos la fecha de modificación usando @last-modified
//                String fechaStr = session.advanced().getMetadataFor(rev).getString("@last-modified");
//                LocalDateTime fechaRev = java.time.OffsetDateTime.parse(fechaStr).toLocalDateTime();
//
//                if (fechaRev.isAfter(inicio) && fechaRev.isBefore(fin)) {
//                    if (rev.getCoordenada() != null) {
//                        trayectoria.add(rev.getCoordenada());
//                    }
//                }
//            }
//        }
//        return trayectoria;
//    }
//
//    // --------------------------------------------------------------------------------
//    // ALCANCE MEDIO 3. ANÁLISIS DE DAÑO DE BATALLA
//    // --------------------------------------------------------------------------------
//
//    @Override
//    public Map<String, Object> analizarDanioDeBatalla(Long mediumId, int revisionesAtras) {
//        String idRaven = "mediums_sql/" + mediumId;
//        Map<String, Object> reporte = new HashMap<>();
//
//        List<Medium> revisiones = registroHistoricoDAO.obtenerHistorial(Medium.class, idRaven);
//        System.out.println("Revisiones obtenidas para mediumId " + mediumId + ": " + revisiones.size());
//
//        if (revisiones.isEmpty()) {
//            throw new NoSuchElementException("No hay historial para el médium con ID: " + mediumId);
//        }
//
//        // La revisión más reciente (estado actual) suele ser la última o primera según el orden de Raven,
//        // asumimos índice 0 como el estado actual en la lista devuelta por el DAO.
//        Medium actual = revisiones.getFirst();
//
//        int targetIndex = Math.min(revisionesAtras, revisiones.size() - 1);
//        Medium pasado = revisiones.get(targetIndex);
//
//        int manaActual = actual.getMana();
//        int manaPasado = pasado.getMana();
//        int diferenciaMana = manaPasado - manaActual;
//
//        reporte.put("mediumNombre", actual.getNombre());
//        reporte.put("manaActual", manaActual);
//        reporte.put("manaPasado", manaPasado);
//        reporte.put("manaPerdido", diferenciaMana);
//        reporte.put("porcentajePerdido", manaPasado > 0 ? ((double) diferenciaMana / manaPasado) * 100 : 0);
//
//        return reporte;
//    }
//}

package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.persistencia.raven.RegistroHistoricoDAO;
import ar.edu.unq.epersgeist.servicios.TrackingService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.ravendb.client.documents.IDocumentStore;
import net.ravendb.client.documents.session.IDocumentSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


@Service
public class TrackingServiceImpl implements TrackingService {

    @Autowired
    private IDocumentStore documentStore;

    @Autowired
    private RegistroHistoricoDAO registroHistoricoDAO;

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public List<Espiritu> historialDeAmos(Long espirituId, LocalDateTime desde) {
        String idRaven = "espiritus_sql/" + espirituId;

        // 1. Buscamos el historial mediante el DAO intacto
        List<Espiritu> revisiones = registroHistoricoDAO.obtenerHistorial(Espiritu.class, idRaven);
        if (revisiones == null || revisiones.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Ordenamos del pasado al presente
        Collections.reverse(revisiones);

        // Usamos LinkedHashSet para mantener orden cronológico libre de repetidos continuos
        Set<Espiritu> amosUnicos = new LinkedHashSet<>();

        // 3. Pasamos la fecha 'desde' a Date para poder comparar de forma directa
        Date fechaDesde = formatLocalDateTime(desde);

        for (Espiritu rev : revisiones) {
            Date fechaRev = rev.getUpdatedAt();

            if (fechaRev != null && fechaRev.after(fechaDesde)) {
                if (rev.getEspirituDominante() != null) {
                    amosUnicos.add(rev.getEspirituDominante());
                }
            }
        }

        return new ArrayList<>(amosUnicos);
    }

    // -----------------------------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------
    // ALCANCE MEDIO 2. EL MAPA DE CALOR TEMPORAL
    // --------------------------------------------------------------------------------

    @Override
    public List<GeoJsonPoint> trayectoriaTemporalDeMedium(Long mediumId) {
        String ravenId = "mediums_mongo/" + mediumId;

        try (IDocumentSession session = documentStore.openSession()) {
            List<ObjectNode> revisionesJson = session.advanced().revisions().getFor(ObjectNode.class, ravenId);

            return revisionesJson.stream()
                    .map(json -> {
                        var coordenadaNode = json.get("coordenada");
                        var coordinatesArray = coordenadaNode.get("coordinates");

                        double longitud = coordinatesArray.get(0).asDouble();
                        double latitud = coordinatesArray.get(1).asDouble();

                        return new GeoJsonPoint(longitud, latitud);
                    })
                    .toList();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    private Date formatLocalDateTime(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }

    // -----------------------------------------------------------------------------------------------------------------
}
