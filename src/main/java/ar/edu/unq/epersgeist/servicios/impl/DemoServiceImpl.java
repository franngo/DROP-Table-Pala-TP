package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.persistencia.mongo.EspirituDAOMongo;
import ar.edu.unq.epersgeist.persistencia.mongo.MediumDAOMongo;
import ar.edu.unq.epersgeist.persistencia.mongo.UbicacionDAOMongo;
import ar.edu.unq.epersgeist.persistencia.neo.UbicacionDAONeo4j;
import ar.edu.unq.epersgeist.persistencia.raven.RegistroHistoricoDAO;
import ar.edu.unq.epersgeist.persistencia.sql.EspirituDAOSQL;
import ar.edu.unq.epersgeist.persistencia.sql.MediumDAOSQL;
import ar.edu.unq.epersgeist.persistencia.sql.UbicacionDAOSQL;
import ar.edu.unq.epersgeist.servicios.*;
import net.ravendb.client.documents.IDocumentStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DemoServiceImpl implements DemoService {

    private final MediumService mediumService;
    private final EspirituService espirituService;
    private final UbicacionService ubicacionService;
    private final TrackingService trackingService;

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    private EspirituDAOMongo espirituDAOMongo;
    @Autowired
    private EspirituDAOSQL espirituDAOSQL;
    @Autowired
    private MediumDAOMongo mediumDAOMongo;
    @Autowired
    private MediumDAOSQL mediumDAOSQL;
    @Autowired
    private UbicacionDAOMongo ubicacionDAOMongo;
    @Autowired
    private UbicacionDAONeo4j ubicacionDAONeo4j;
    @Autowired
    private UbicacionDAOSQL ubicacionDAOSQL;
    @Autowired
    private RegistroHistoricoDAO registroHistorico;
    @Autowired
    private IDocumentStore documentStore;

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    public DemoServiceImpl(
            MediumService mediumService,
            EspirituService espirituService,
            UbicacionService ubicacionService,
            TrackingService trackingService
    ) {
        this.mediumService = mediumService;
        this.espirituService = espirituService;
        this.ubicacionService = ubicacionService;
        this.trackingService = trackingService;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    public List<Espiritu> historialDeAmos() {
        System.out.println("\n========================================================================");
        System.out.println("🚀 DEMO INTERACTIVO: CRISIS DE IDENTIDAD (HISTORIAL DE AMOS)");
        System.out.println("========================================================================\n");

        // ----------------------------------------------------------------------
        // 1. SETUP DE UBICACION
        // ----------------------------------------------------------------------
        List<Coordenada> coordenadasUbicacion = List.of(
                new Coordenada(-34.81, -58.22),
                new Coordenada(-34.82, -58.24),
                new Coordenada(-34.80, -58.20)
        );
        Ubicacion ubicacion = ubicacionService.crear(
                new Santuario("Templo de la Luz", 100),
                coordenadasUbicacion
        );
        System.out.println("[SETUP] Ubicación '" + ubicacion.getNombre() + "' creada exitosamente.");

        // ----------------------------------------------------------------------
        // 2. SETUP DE ESPIRITUS
        // ----------------------------------------------------------------------
        Espiritu amo1 = espirituService.crear(new Angelical("Primer Amo", ubicacion), new Coordenada(-34.81, -58.22));
        Espiritu amo2 = espirituService.crear(new Angelical("Segundo Amo", ubicacion), new Coordenada(-34.81, -58.22));
        Espiritu amo3 = espirituService.crear(new Angelical("Tercer Amo", ubicacion), new Coordenada(-34.81, -58.22));
        Espiritu subordinado = espirituService.crear(new Angelical("Espiritu Subordinado", ubicacion), new Coordenada(-34.80, -58.20));

        System.out.println("[SETUP] Entidades creadas en MySQL y persistidas en RavenDB.");
        System.out.println("        • Subordinado Inicial: " + subordinado.getNombre() + " [Dominado = " + subordinado.estaDominado() + "]");

        // ----------------------------------------------------------------------
        // 3. INICIO DE AUDITORÍA TEMPORAL
        // ----------------------------------------------------------------------
        LocalDateTime desde = LocalDateTime.now().minusSeconds(1);
        System.out.println("[AUDIT] Punto de control temporal fijado en: " + desde);

        // ----------------------------------------------------------------------
        // 4. EJECUCIÓN DE LAS DOMINACIONES SUCESIVAS
        // ----------------------------------------------------------------------

        // TIMELINE 1: Primer Amo
        System.out.println("\n ─── HITO 1: PRIMER AMO ───");
        System.out.println("[ACCION 1] Invocando dominar()...");
        espirituService.dominar(amo1.getId(), subordinado.getId());
        subordinado = espirituService.recuperar(subordinado.getId());
        System.out.println("           ↳ Estado actual: [Dominado = " + subordinado.estaDominado() + "] por " + subordinado.getEspirituDominante().getNombre());

        System.out.println("[ACCION 2] Liberando espíritu subordinado...");
        subordinado.setEspirituDominante(null);
        espirituService.actualizar(subordinado);
        subordinado = espirituService.recuperar(subordinado.getId());
        System.out.println("           ↳ Estado actual: [Dominado = " + subordinado.estaDominado() + "]");

        // TIMELINE 2: Segundo Amo
        System.out.println("\n ─── HITO 2: SEGUNDO AMO ───");
        System.out.println("[ACCION 3] Invocando dominar()...");
        espirituService.dominar(amo2.getId(), subordinado.getId());
        subordinado = espirituService.recuperar(subordinado.getId());
        System.out.println("           ↳ Estado actual: [Dominado = " + subordinado.estaDominado() + "] por " + subordinado.getEspirituDominante().getNombre());

        System.out.println("[ACCION 4] Liberando espíritu subordinado...");
        subordinado.setEspirituDominante(null);
        espirituService.actualizar(subordinado);
        subordinado = espirituService.recuperar(subordinado.getId());
        System.out.println("           ↳ Estado actual: [Dominado = " + subordinado.estaDominado() + "]");

        // TIMELINE 3: Tercer Amo
        System.out.println("\n ─── HITO 3: TERCER AMO ───");
        System.out.println("[ACCION 5] Invocando dominar()...");
        espirituService.dominar(amo3.getId(), subordinado.getId());
        subordinado = espirituService.recuperar(subordinado.getId());
        System.out.println("           ↳ Estado actual: [Dominado = " + subordinado.estaDominado() + "] por " + subordinado.getEspirituDominante().getNombre());

        System.out.println("[ACCION 6] Liberando espíritu subordinado final...");
        subordinado.setEspirituDominante(null);
        espirituService.actualizar(subordinado);
        subordinado = espirituService.recuperar(subordinado.getId());
        System.out.println("           ↳ Estado actual final: [Dominado = " + subordinado.estaDominado() + "]");

        // ----------------------------------------------------------------------
        // 5. CONSULTA FINAL AL SERVICE DE TELEMETRÍA
        // ----------------------------------------------------------------------
        System.out.println("\n[CONSULTA] Extrayendo historial cronológico desde RavenDB...");
        return trackingService.historialDeAmos(subordinado.getId(), desde);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    public List<GeoJsonPoint> demoMapaDeCalorTemporal() {
        System.out.println("\n========================================================================");
        System.out.println("🚀 DEMO: MAPA DE CALOR TEMPORAL (3 UBICACIONES CONECTADAS)");
        System.out.println("========================================================================\n");

        // 1. SETUP DE PUNTOS CENTRALES
        Coordenada coordenada1 = new Coordenada(-34.800, -58.200);
        Coordenada coordenada2 = new Coordenada(-34.810, -58.210);
        Coordenada coordenada3 = new Coordenada(-34.820, -58.220);

        // 2. SETUP DE POLÍGONOS (Cada ubicación requiere una lista de 3 coordenadas)
        List<Coordenada> areaU1 = List.of(
                coordenada1,
                new Coordenada(-34.801, -58.201),
                new Coordenada(-34.802, -58.202)
        );
        List<Coordenada> areaU2 = List.of(
                coordenada2,
                new Coordenada(-34.811, -58.211),
                new Coordenada(-34.812, -58.212)
        );
        List<Coordenada> areaU3 = List.of(
                coordenada3,
                new Coordenada(-34.821, -58.221),
                new Coordenada(-34.822, -58.222)
        );

        // Guardamos las 3 ubicaciones en las bases de datos (SQL, Mongo, Neo4j)
        Ubicacion u1 = ubicacionService.crear(new Santuario("Santuario Alfa", 100), areaU1);
        Ubicacion u2 = ubicacionService.crear(new Santuario("Santuario Beta", 100), areaU2);
        Ubicacion u3 = ubicacionService.crear(new Santuario("Santuario Gamma", 100), areaU3);
        System.out.println("[SQL] Se crearon las ubicaciones Alfa, Beta y Gamma");

        // 3. CONEXIÓN EN NEO4J
        ubicacionService.conectar(u1.getId(), u2.getId(), 5L);
        ubicacionService.conectar(u2.getId(), u3.getId(), 5L);
        ubicacionService.conectar(u3.getId(), u1.getId(), 5L);
        System.out.println("[NEO4J] Se conectaron los nodos: Alfa -> Beta -> Gamma -> Alfa.");

        // Creamos el médium con maná de sobra (200) para costear los viajes en el Santuario Alfa (u1)
        Medium medium = mediumService.crear(new Medium("Caminante Astral", 200, 180, u1), coordenada1);
        System.out.println("[SETUP] Médium creado en el Santuario Alfa.");

        // 4. TRAYECTO (El médium visita todas las ubicaciones conectadas)

        // Movimiento 1: De Alfa a Beta (Coordenada c2)
        System.out.println("[TRAYECTO] Moviéndose a Santuario Beta...");
        mediumService.mover(medium.getId(), coordenada2.getLatitud(), coordenada2.getLongitud());

        // Movimiento 2: De Beta a Gamma (Coordenada c3)
        System.out.println("[TRAYECTO] Moviéndose a Santuario Gamma...");
        mediumService.mover(medium.getId(), coordenada3.getLatitud(), coordenada3.getLongitud());

        // Movimiento 3: De Gamma volviendo a Alfa (Coordenada c1)
        System.out.println("[TRAYECTO] Regresando a Santuario Alfa...");
        mediumService.mover(medium.getId(), coordenada1.getLatitud(), coordenada1.getLongitud());

        // 5. CONSULTA DE TELEMETRÍA
        System.out.println("\n[CONSULTA] Extrayendo mapa de calor temporal desde el TrackingService...");
        return trackingService.trayectoriaTemporalDeMedium(medium.getId());
    }

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    public void demoCostoMoverseUltimaUbicacion() {
        System.out.println("\n========================================================================");
        System.out.println("🚀 DEMO: COSTO DE MOVERSE DESDE LA ÚLTIMA UBICACIÓN (2 UBICACIONES)");
        System.out.println("========================================================================\n");

        // 1. SETUP DE PUNTOS CENTRALES (2 Ubicaciones)
        Coordenada coordenada1 = new Coordenada(-34.800, -58.200);
        Coordenada coordenada2 = new Coordenada(-34.810, -58.210);

        // 2. SETUP DE POLÍGONOS
        List<Coordenada> areaU1 = List.of(
                coordenada1,
                new Coordenada(-34.801, -58.201),
                new Coordenada(-34.802, -58.202)
        );
        List<Coordenada> areaU2 = List.of(
                coordenada2,
                new Coordenada(-34.811, -58.211),
                new Coordenada(-34.812, -58.212)
        );

        // Guardamos las ubicaciones en las bases de datos
        Ubicacion u1 = ubicacionService.crear(new Santuario("Santuario Alfa", 100), areaU1);
        Ubicacion u2 = ubicacionService.crear(new Santuario("Santuario Beta", 100), areaU2);
        System.out.println("[SQL/MONGO/NEO4J] Se crearon las ubicaciones Alfa y Beta.");

        // 3. CONEXIÓN EN NEO4J (Camino de ida y vuelta para simular el movimiento)
        ubicacionService.conectar(u1.getId(), u2.getId(), 8L);
        ubicacionService.conectar(u2.getId(), u1.getId(), 8L);
        System.out.println("[NEO4J] Se conectaron los nodos: Alfa <-> Beta con costo 8.");

        // Creamos el médium inicialmente en el Santuario Alfa (u1)
        Medium medium = mediumService.crear(new Medium("Caminante Nocturno", 150, 100, u1), coordenada1);
        System.out.println("[SETUP] Médium creado en el Santuario Alfa con 150 de maná.");

        // 4. TRAYECTO: Movimiento hacia la segunda ubicación
        System.out.println("[TRAYECTO] Moviéndose a Santuario Beta...");
        mediumService.mover(medium.getId(), coordenada2.getLatitud(), coordenada2.getLongitud());

        // 5. CONSULTA DE TELEMETRÍA (Costo desde la última ubicación en RavenDB)
        System.out.println("\n[CONSULTA] Extrayendo reporte de costo de movimiento desde el TrackingService...");
        Map<String, Object> reporte = trackingService.costoDeMoverseDesdeLaUltimaUbicacion(medium.getId());

        System.out.println("[RAVENDB RESULT] Telemetría de movimiento:");
        System.out.println("   Nombre: " + reporte.get("mediumNombre"));
        System.out.println("   Maná Pasado (Origen): " + reporte.get("manaPasado"));
        System.out.println("   Maná Actual (Destino): " + reporte.get("manaActual"));
        System.out.println("   Maná Gastado: " + reporte.get("manaGastado"));
        System.out.println("   Porcentaje Drenado/Variación: " + String.format("%.2f", reporte.get("porcentajeGastado")) + "%");
        System.out.println("========================================================================\n");

//        return reporte;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    public void deleteAll() {
        System.out.println("🧹 Iniciando limpieza total de bases de datos...");

        // A. Primero Espíritus
        espirituDAOMongo.deleteAll();
        espirituDAOSQL.deleteAll();

        // B. Luego eliminamos los Médiums
        mediumDAOMongo.deleteAll();
        mediumDAOSQL.deleteAll();

        // C. Por último las Ubicaciones
        ubicacionDAOMongo.deleteAll();
        ubicacionDAONeo4j.deleteAll();
        ubicacionDAOSQL.deleteAll();

        // D. Limpieza y recreación de la base de RavenDB
        registroHistorico.reiniciarBaseDeDatos();
    }
}
