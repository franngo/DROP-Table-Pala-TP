package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.servicios.*;
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

    public DemoServiceImpl(MediumService mediumService, EspirituService espirituService,
                           UbicacionService ubicacionService, TrackingService trackingService) {
        this.mediumService = mediumService;
        this.espirituService = espirituService;
        this.ubicacionService = ubicacionService;
        this.trackingService = trackingService;
    }

    // -----------------------------------------------------------------------------
    // DEMO 1: LA CRISIS DE IDENTIDAD
    // -----------------------------------------------------------------------------
    @Override
    public void demoCrisisDeIdentidad() throws Exception {
        System.out.println("\n=== INICIANDO DEMO 1: LA CRISIS DE IDENTIDAD (REVISIONS DE ESPÍRITUS) ===");

        // Creamos una lista de coordenadas para definir el polígono de la ubicación (mínimo 4 puntos cerrados para GeoJson si valida anillo, o los que use tu lógica)
        Coordenada puntoComun = new Coordenada(-34.60, -58.38);
        List<Coordenada> areaUbicacion = List.of(
                new Coordenada(-34.59, -58.39),
                new Coordenada(-34.59, -58.37),
                new Coordenada(-34.61, -58.37),
                new Coordenada(-34.59, -58.39) // Cierre del polígono
        );

        Ubicacion ubi = ubicacionService.crear(new Santuario("Santuario Alfa", 80), areaUbicacion);
        Medium m1 = mediumService.crear(new Medium("John Constantine", 100, 100, ubi), puntoComun);
        Demonio esp = (Demonio) espirituService.crear(new Demonio("Legión", ubi), puntoComun);

        LocalDateTime inicioDemo = LocalDateTime.now().minusSeconds(1);
        Thread.sleep(1000);

        System.out.println("-> Conectando espíritu con el médium...");
        espirituService.conectar(esp.getId(), m1.getId());
        Thread.sleep(1000);

        int cambios = trackingService.cantidadDeCambiosDeBando(esp.getId(), inicioDemo);
        System.out.println("[RAVENDB RESULT] Cambios de bando detectados: " + cambios);
        System.out.println("========================================================================\n");
    }

    // -----------------------------------------------------------------------------
    // DEMO 2: EL MAPA DE CALOR TEMPORAL
    // -----------------------------------------------------------------------------
    @Override
    public void demoMapaDeCalorTemporal() throws Exception {
        System.out.println("\n=== INICIANDO DEMO 2: EL MAPA DE CALOR TEMPORAL ===");

        Coordenada puntoComun = new Coordenada(-34.60, -58.38);
        List<Coordenada> areaUbicacion = List.of(
                new Coordenada(-34.59, -58.39),
                new Coordenada(-34.59, -58.37),
                new Coordenada(-34.61, -58.37),
                new Coordenada(-34.59, -58.39)
        );

        Ubicacion ubi = ubicacionService.crear(new Santuario("Santuario Beta", 70), areaUbicacion);
        Medium m = mediumService.crear(new Medium("Hermano Ruiz", 100, 100, ubi), puntoComun);

        LocalDateTime inicioTrayecto = LocalDateTime.now();
        Thread.sleep(1000);

        System.out.println("-> Simulando caminata del médium...");
        try {
            mediumService.mover(m.getId(), -34.601, -58.381);
            Thread.sleep(1000);
            mediumService.mover(m.getId(), -34.602, -58.382);
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Nota: Movimiento registrado en el historial.");
        }

        LocalDateTime finTrayecto = LocalDateTime.now();

        List<GeoJsonPoint> puntos = trackingService.trayectoriaTemporalDeMedium(m.getId(), inicioTrayecto, finTrayecto);
        System.out.println("[RAVENDB RESULT] Ruta histórica reconstruída:");
        puntos.forEach(p -> System.out.println("   📍 Coordenada pasada: [" + p.getX() + ", " + p.getY() + "]"));
        System.out.println("========================================================================\n");
    }

    // -----------------------------------------------------------------------------
    // DEMO 3: ANÁLISIS DE DAÑO DE BATALLA
    // -----------------------------------------------------------------------------
    @Override
    public void demoDanioDeBatalla() throws Exception {
        System.out.println("\n=== INICIANDO DEMO 3: ANÁLISIS DE DAÑO DE BATALLA ===");

        Coordenada puntoComun = new Coordenada(-34.60, -58.38);
        List<Coordenada> areaUbicacion = List.of(
                new Coordenada(-34.59, -58.39),
                new Coordenada(-34.59, -58.37),
                new Coordenada(-34.61, -58.37),
                new Coordenada(-34.59, -58.39)
        );

        Ubicacion ubi = ubicacionService.crear(new Santuario("Santuario Gamma", 90), areaUbicacion);

        Medium atacante = mediumService.crear(new Medium("Papa Midnite", 100, 100, ubi), puntoComun);
        Medium victima = mediumService.crear(new Medium("John Constantine", 100, 100, ubi), puntoComun);

        Angelical angel = (Angelical) espirituService.crear(new Angelical("Azael", ubi), puntoComun);
        espirituService.conectar(angel.getId(), atacante.getId());

        Thread.sleep(1000);
        System.out.println("-> Ejecutando Exorcismo...");
        mediumService.exorcizar(atacante.getId(), victima.getId());
        Thread.sleep(1000);

        Map<String, Object> reporte = trackingService.analizarDanioDeBatalla(victima.getId(), 1);
        System.out.println("[RAVENDB RESULT] Telemetría de combate:");
        System.out.println("   Nombre: " + reporte.get("mediumNombre"));
        System.out.println("   Maná Inicial: " + reporte.get("manaPasado"));
        System.out.println("   Maná Actual: " + reporte.get("manaActual"));
        System.out.println("   Porcentaje Drenado: " + String.format("%.2f", reporte.get("porcentajePerdido")) + "%");
        System.out.println("========================================================================\n");
    }
}