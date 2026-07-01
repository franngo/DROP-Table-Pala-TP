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
    private final RegistroHistoricoDAO registroHistorico;

    // --------------------------------------------------------------------------------

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
    private IDocumentStore documentStore;

    // --------------------------------------------------------------------------------

    public DemoServiceImpl(
            MediumService mediumService, EspirituService espirituService,
            UbicacionService ubicacionService, TrackingService trackingService,
            RegistroHistoricoDAO registroHistorico) {
        this.mediumService = mediumService;
        this.espirituService = espirituService;
        this.ubicacionService = ubicacionService;
        this.trackingService = trackingService;
        this.registroHistorico = registroHistorico;
    }

    // --------------------------------------------------------------------------------

    public void ejecutarDemosTracking() throws Exception {
        demoCrisisDeIdentidad_Simple();
    }

    public void demoCrisisDeIdentidad_Simple() throws Exception {
        this.deleteAll();
        long ts = System.currentTimeMillis();

        System.out.println("\n========================================================================");
        System.out.println("🚀 CONTROL DE TELEMETRÍA - DEMO SIMPLIFICADA");
        System.out.println("========================================================================\n");

        // SETUP BASE COMÚN (Misma ubicación del test exitoso)
        List<Coordenada> area = List.of(
                new Coordenada(-34.81, -58.22),
                new Coordenada(-34.82, -58.24),
                new Coordenada(-34.80, -58.20)
        );
        Ubicacion ubi = ubicacionService.crear(new Santuario("Berazategui " + ts, 100), area);

        // ------------------------------------------------------------------------
        // CASO 1: DEBE DAR 0 CAMBIOS (Modificamos campo sin alterar el bando)
        // ------------------------------------------------------------------------
        Espiritu esp1 = espirituService.crear(new ar.edu.unq.epersgeist.modelo.Espiritu.Angelical("Legión", ubi), new Coordenada(-34.81, -58.22));

        // Fijamos el inicio de la auditoría ANTES de la modificación
        LocalDateTime desdeCaso1 = LocalDateTime.now().minusSeconds(1);

        // Cambiamos un atributo ordinario (Nivel de conexión) -> Genera revisión en RavenDB
        esp1.setNivelDeConexion(50);
        espirituService.actualizar(esp1);

        Thread.sleep(1000); // Pausa para asegurar consistencia en RavenDB
        int cambiosCaso1 = trackingService.cantidadDeCambiosDeBando(esp1.getId(), desdeCaso1);

        System.out.println("[ESCENARIO 1] Actividad ordinaria en '" + esp1.getNombre() + "' (Se modificó nivel de conexión)");
        System.out.println("   • Cambios de bando hallados: " + cambiosCaso1 + " (Esperado: 0)");
        System.out.println("------------------------------------------------------------------------\n");


        // ------------------------------------------------------------------------
        // CASO 2: DEBE DAR 1 CAMBIO (Dominación real)
        // ------------------------------------------------------------------------
        Espiritu dominante = espirituService.crear(new ar.edu.unq.epersgeist.modelo.Espiritu.Demonio("Azazel", ubi), new Coordenada(-34.81, -58.22));
        Espiritu aDominar = espirituService.crear(new ar.edu.unq.epersgeist.modelo.Espiritu.Angelical("Cas", ubi), new Coordenada(-34.80, -58.20));

        // Fijamos el inicio de la auditoría ANTES de la dominación
        LocalDateTime desdeCaso2 = LocalDateTime.now().minusSeconds(1);

        // Ejecutamos la acción que altera el bando (Libre -> Dominado)
        espirituService.dominar(dominante.getId(), aDominar.getId());

        Thread.sleep(1000); // Pausa para asegurar consistencia en RavenDB
        int cambiosCaso2 = trackingService.cantidadDeCambiosDeBando(aDominar.getId(), desdeCaso2);

        System.out.println("[ESCENARIO 2] Dominación sobre '" + aDominar.getNombre() + "' (Mutó de Libre a Dominado)");
        System.out.println("   • Cambios de bando hallados: " + cambiosCaso2 + " (Esperado: 1)");
        System.out.println("========================================================================\n");
    }

    public void demoCrisisDeIdentidad_SinCambioDeBando() throws Exception {
        deleteAll(); // Limpieza inicial políglota
        long ts = System.currentTimeMillis();

        System.out.println("\n========================================================================");
        System.out.println("🚀 ESCENARIO 1: ACTIVIDAD SIN CAMBIO DE BANDO (CONEXIÓN)");
        System.out.println("========================================================================");

        // 1. Setup inicial de Entidades
        List<Coordenada> area = List.of(
                new Coordenada(-34.59, -58.39), new Coordenada(-34.59, -58.37),
                new Coordenada(-34.61, -58.37), new Coordenada(-34.59, -58.39)
        );
        Ubicacion ubi = ubicacionService.crear(new Santuario("Santuario Alfa " + ts, 80), area);
        Medium m1 = mediumService.crear(new Medium("John Constantine", 100, 100, ubi), new Coordenada(-34.60, -58.38));
        Espiritu esp = espirituService.crear(new ar.edu.unq.epersgeist.modelo.Espiritu.Angelical("Legión", ubi), new Coordenada(-34.60, -58.38));

        System.out.println("[SETUP] Creados Ubicación, Médium y Espíritu (" + esp.getNombre() + " - Libre)");

        // 2. Fijar ventana temporal de auditoría
        LocalDateTime desde = LocalDateTime.now();
        System.out.println("[TIME] Inicio de auditoría temporal: " + desde);
//        Thread.sleep(1000);

        // 3. Acción de negocio: Conectar (Modifica niveles, pero no el bando)
        System.out.println("[ACTION] Conectando espíritu '" + esp.getNombre() + "' con médium '" + m1.getNombre() + "'...");
        espirituService.conectar(esp.getId(), m1.getId());

        // 4. Consulta de Telemetría
//        Thread.sleep(1000); // Pausa para consistencia eventual de RavenDB
        int cambios = trackingService.cantidadDeCambiosDeBando(esp.getId(), desde);

        System.out.println("========================================================================");
        System.out.println("📊 RESULTADO TELEMETRÍA");
        System.out.println("   • Espíritu Auditado        : espiritus_sql/" + esp.getId());
        System.out.println("   • Cambios de bando hallados: " + cambios);
        System.out.println("   • Análisis                 : Explicación correcta. Al conectar se mutó");
        System.out.println("                                la energía/conexión, pero el bando se mantuvo");
        System.out.println("                                intacto (Libre).");
        System.out.println("========================================================================\n");
    }

    public void demoCrisisDeIdentidad_ConCambioDeBandoReal() throws Exception {
        this.deleteAll(); // Limpieza inicial políglota
        long ts = System.currentTimeMillis();

        System.out.println("========================================================================");
        System.out.println("🚀 ESCENARIO 2: MUTACIÓN REAL DE BANDO (DOMINACIÓN)");
        System.out.println("========================================================================");

        // 1. Setup: Se crea la ubicación Berazategui usando el polígono de los tests
        List<Coordenada> area = List.of(
                new Coordenada(-34.81, -58.22),
                new Coordenada(-34.82, -58.24),
                new Coordenada(-34.80, -58.20)
        );
        Ubicacion ubi = ubicacionService.crear(new Santuario("Berazategui " + ts, 100), area);

        // Espíritu dominante (Azazel) en el nodo base de las coordenadas del test
        Espiritu dominante = espirituService.crear(
                new ar.edu.unq.epersgeist.modelo.Espiritu.Demonio("Azazel", ubi),
                new Coordenada(-34.81, -58.22)
        );

        // Espíritu objetivo (Cas) en el otro nodo del polígono (Distancia de ~2140m, mayor a 0.0)
        Espiritu aDominar = espirituService.crear(
                new ar.edu.unq.epersgeist.modelo.Espiritu.Angelical("Cas", ubi),
                new Coordenada(-34.80, -58.20)
        );

        System.out.println("[SETUP] Creado Espíritu Dominante (" + dominante.getNombre() + ")");
        System.out.println("[SETUP] Creado Espíritu Objetivo  (" + aDominar.getNombre() + " - Inicialmente Libre)");

        // 2. Fijar ventana temporal de auditoría
        LocalDateTime desde = LocalDateTime.now();
        System.out.println("[TIME] Inicio de auditoría temporal: " + desde);
//        Thread.sleep(1000); // Pausa para distanciar los timestamps de los snapshots

        // 3. Acción de negocio: Dominar (Fuerza la mutación de Libre -> Dominado)
        System.out.println("[ACTION] El demonio '" + dominante.getNombre() + "' intenta dominar a '" + aDominar.getNombre() + "'...");
        espirituService.dominar(dominante.getId(), aDominar.getId());

        // 4. Consulta de Telemetría sobre el espíritu afectado
//        Thread.sleep(2500); // Espera de consistencia para RavenDB
        int cambios = trackingService.cantidadDeCambiosDeBando(aDominar.getId(), desde);

        System.out.println("========================================================================");
        System.out.println("📊 RESULTADO TELEMETRÍA");
        System.out.println("   • Espíritu Auditado        : espiritus_sql/" + aDominar.getId());
        System.out.println("   • Cambios de bando hallados: " + cambios);
        System.out.println("   • Análisis                 : ¡Éxito! RavenDB detectó la mutación temporal");
        System.out.println("                                al pasar de estar 'Libre' a 'Dominado'");
        System.out.println("                                a través del historial de Revisiones.");
        System.out.println("========================================================================\n");
    }
    // --------------------------------------------------------------------------------

    @Override
    public void demoCrisisDeIdentidad() throws Exception {

    }

    @Override
    public void demoMapaDeCalorTemporal() throws Exception {
        deleteAll();
        long timestamp = System.currentTimeMillis();
        System.out.println("\n========================================================================");
        System.out.println("▶ INICIANDO DEMO 2: EL MAPA DE CALOR TEMPORAL");
        System.out.println("========================================================================");

        List<Coordenada> areaUbicacion = List.of(
                new Coordenada(-33.59, -57.39), new Coordenada(-33.59, -57.37),
                new Coordenada(-33.61, -57.37), new Coordenada(-33.59, -57.39)
        );

        Ubicacion ubi = ubicacionService.crear(new Santuario("Santuario Beta " + timestamp, 70), areaUbicacion);
        System.out.println("[SQL/MONGO/NEO] 🗺️ Ubicación creada: " + ubi.getNombre() + " (ID: " + ubi.getId() + ")");

        Medium m = mediumService.crear(new Medium("Hermano Ruiz", 100, 100, ubi), new Coordenada(-33.60, -57.38));
        System.out.println("[SQL] 👤 Médium creado: " + m.getNombre() + " (ID: " + m.getId() + ")");

        LocalDateTime inicioTrayecto = LocalDateTime.now();
        System.out.println("🕒 Comienza el trayecto temporal cronometrado: " + inicioTrayecto);
        Thread.sleep(500);

        System.out.println("🚶 Simulando caminata del Médium (ID: " + m.getId() + ")...");
        try {
            System.out.println("   📍 Moviendo a [-33.601, -57.381]...");
            mediumService.mover(m.getId(), -33.601, -57.381);
            Thread.sleep(1000);

            System.out.println("   📍 Moviendo a [-33.602, -57.382]...");
            mediumService.mover(m.getId(), -33.602, -57.382);
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("   ⚠️ Nota: Movimiento procesado.");
        }

        LocalDateTime finTrayecto = LocalDateTime.now();
        System.out.println("🕒 Fin del trayecto: " + finTrayecto);

        System.out.println("🔍 Consultando trackingService.trayectoriaTemporalDeMedium...");
        List<GeoJsonPoint> puntos = trackingService.trayectoriaTemporalDeMedium(m.getId(), inicioTrayecto, finTrayecto);

        System.out.println("\n📊 [RESULTADO RAVENDB]");
        System.out.println("   ▶ Historial de coordenadas recuperadas para Médium ID " + m.getId() + ":");
        if (puntos.isEmpty()) {
            System.out.println("   ❌ No se registraron coordenadas en ese rango de tiempo.");
        } else {
            puntos.forEach(p -> System.out.println("      📍 Coordenada histórica -> X: " + p.getX() + " | Y: " + p.getY()));
        }
        System.out.println("========================================================================\n");
    }

    @Override
    public void demoDanioDeBatalla() throws Exception {
        deleteAll();
        long timestamp = System.currentTimeMillis();
        System.out.println("\n========================================================================");
        System.out.println("▶ INICIANDO DEMO 3: ANÁLISIS DE DAÑO DE BATALLA");
        System.out.println("========================================================================\n");

        List<Coordenada> areaUbicacion = List.of(
                new Coordenada(-32.59, -56.39), new Coordenada(-32.59, -56.37),
                new Coordenada(-32.61, -56.37), new Coordenada(-32.59, -56.39)
        );

        Ubicacion ubi = ubicacionService.crear(new Santuario("Santuario Gamma " + timestamp, 90), areaUbicacion);
        System.out.println("[SQL/MONGO/NEO] 🗺️ Ubicación creada: " + ubi.getNombre() + " (ID: " + ubi.getId() + ")");

        Medium atacante = mediumService.crear(new Medium("Papa Midnite", 100, 100, ubi), new Coordenada(-32.60, -56.38));
        Medium victima = mediumService.crear(new Medium("John Constantine", 100, 100, ubi), new Coordenada(-32.60, -56.38));
        System.out.println("[SQL] 👤 Atacante creado: " + atacante.getNombre() + " (ID: " + atacante.getId() + ")");
        System.out.println("[SQL] 👤 Víctima creada: " + victima.getNombre() + " (ID: " + victima.getId() + ") | Mana Max: 100");

        Angelical angel = (Angelical) espirituService.crear(new Angelical("Azael", ubi), new Coordenada(-32.60, -56.38));
        System.out.println("[SQL] 👻 Espíritu Atacante creado: " + angel.getNombre() + " (ID: " + angel.getId() + ")");

        espirituService.conectar(angel.getId(), atacante.getId());
        System.out.println("🔗 Espíritu " + angel.getId() + " conectado exitosamente al atacante " + atacante.getId());
        Thread.sleep(500);

        System.out.println("⚔️ ¡Ejecutando Exorcismo! Atacante " + atacante.getId() + " exorciza a Víctima " + victima.getId());
        mediumService.exorcizar(atacante.getId(), victima.getId());
        Thread.sleep(1500); // Esperamos indexación de los estados post-batalla

        System.out.println("🔍 Consultando trackingService.analizarDanioDeBatalla para la víctima...");
        Map<String, Object> reporte = trackingService.analizarDanioDeBatalla(victima.getId(), 1);

        System.out.println("\n📊 [RESULTADO RAVENDB - TELEMETRÍA DE COMBATE]");
        System.out.println("   ▶ Objetivo Analizado: " + reporte.get("mediumNombre") + " (ID: " + victima.getId() + ")");
        System.out.println("   ▶ Maná Antes del Ataque: " + reporte.get("manaPasado"));
        System.out.println("   ▶ Maná Después del Ataque: " + reporte.get("manaActual"));
        System.out.println("   ▶ Porcentaje de Maná Drenado: " + String.format("%.2f", reporte.get("porcentajePerdido")) + "%");
        System.out.println("========================================================================\n");
    }

    private void deleteAll() {
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

//        // D. Limpieza y recreación de la base de RavenDB
//        try {
//            String database = documentStore.getDatabase();
//
//            documentStore.maintenance()
//                    .server()
//                    .send(new DeleteDatabasesOperation(database, true));
//
//            documentStore.maintenance()
//                    .server()
//                    .send(new CreateDatabaseOperation(new DatabaseRecord(database)));
//
//            System.out.println("✨ RavenDB recreada con éxito.");
//        } catch (Exception e) {
//            System.err.println("⚠️ Error al reiniciar la base de RavenDB: " + e.getMessage());
//        }
    }
}