package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.mongo.EspirituDAOMongo;
import ar.edu.unq.epersgeist.persistencia.mongo.MediumDAOMongo;
import ar.edu.unq.epersgeist.persistencia.mongo.UbicacionDAOMongo;
import ar.edu.unq.epersgeist.persistencia.neo.UbicacionDAONeo4j;
import ar.edu.unq.epersgeist.persistencia.sql.EspirituDAOSQL;
import ar.edu.unq.epersgeist.persistencia.sql.MediumDAOSQL;
import ar.edu.unq.epersgeist.persistencia.sql.UbicacionDAOSQL;
import ar.edu.unq.epersgeist.servicios.*;
import net.ravendb.client.documents.IDocumentStore;
import net.ravendb.client.documents.operations.revisions.ConfigureRevisionsOperation;
import net.ravendb.client.documents.operations.revisions.RevisionsCollectionConfiguration;
import net.ravendb.client.documents.operations.revisions.RevisionsConfiguration;
import net.ravendb.client.serverwide.DatabaseRecord;
import net.ravendb.client.serverwide.operations.CreateDatabaseOperation;
import net.ravendb.client.serverwide.operations.DeleteDatabasesOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    private UbicacionDAOSQL registroHistorico;
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
        try {
            String database = documentStore.getDatabase();

            documentStore.maintenance()
                    .server()
                    .send(new DeleteDatabasesOperation(database, true));

            documentStore.maintenance()
                    .server()
                    .send(new CreateDatabaseOperation(new DatabaseRecord(database)));

            // 🔴 FORZAR LAS REVISIONES JUSTO DESPUÉS DE RECREAR LA BASE
            documentStore.maintenance().send(
                    new ConfigureRevisionsOperation(
                            new RevisionsConfiguration() {{
                                setDefaultConfig(new RevisionsCollectionConfiguration() {{
                                    setDisabled(false);
                                    setMinimumRevisionsToKeep(25L);
                                }});
                            }}
                    ));

            System.out.println("✨ RavenDB recreada con éxito.");
        } catch (Exception e) {
            System.err.println("⚠️ Error al reiniciar la base de RavenDB: " + e.getMessage());
        }
    }
}