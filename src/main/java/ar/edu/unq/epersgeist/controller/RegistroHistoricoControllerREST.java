package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.servicios.RegistroHistoricoService;
import ar.edu.unq.epersgeist.servicios.RestauracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/registro-historico")
public class RegistroHistoricoControllerREST {

    @Autowired
    private RegistroHistoricoService registroHistoricoService;

    @Autowired
    private RestauracionService restauracionService;

    // ============================================================================
    // HITO 1: CONSULTAS DE HISTORIAL CRONOLÓGICO (Todas las revisiones)
    // ============================================================================

    @GetMapping("/espiritus/{id}/historial")
    @ResponseStatus(HttpStatus.OK)
    public List<Espiritu> obtenerHistorialEspiritu(@PathVariable Long id) {
        return registroHistoricoService.obtenerHistorial(Espiritu.class, "espiritus_sql/" + id);
    }

    @GetMapping("/mediums/{id}/historial")
    @ResponseStatus(HttpStatus.OK)
    public List<Medium> obtenerHistorialMedium(@PathVariable Long id) {
        return registroHistoricoService.obtenerHistorial(Medium.class, "mediums_sql/" + id);
    }

    @GetMapping("/ubicaciones/{id}/historial")
    @ResponseStatus(HttpStatus.OK)
    public List<Ubicacion> obtenerHistorialUbicacion(@PathVariable Long id) {
        return registroHistoricoService.obtenerHistorial(Ubicacion.class, "ubicaciones_sql/" + id);
    }

    // ============================================================================
    // HITO 2 (PARTE A): ESTADO DE UNA ENTIDAD EN UNA FECHA DETERMINADA
    // ============================================================================

    @GetMapping("/espiritus/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Espiritu obtenerEstadoEnFechaEspiritu(
            @PathVariable Long id,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha
    ) {
        return registroHistoricoService.obtenerEstadoEnFecha(Espiritu.class, "espiritus_sql/" + id, fecha);
    }

    @GetMapping("/mediums/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Medium obtenerEstadoEnFechaMedium(
            @PathVariable Long id,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha
    ) {
        return registroHistoricoService.obtenerEstadoEnFecha(Medium.class, "mediums_sql/" + id, fecha);
    }

    @GetMapping("/ubicaciones/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Ubicacion obtenerEstadoEnFechaUbicacion(
            @PathVariable Long id,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha
    ) {
        return registroHistoricoService.obtenerEstadoEnFecha(Ubicacion.class, "ubicaciones_sql/" + id, fecha);
    }

    // ============================================================================
    // HITO 2 (PARTE B): EL BOTÓN DE RESTAURACIÓN GLOBAL (Viaje en el tiempo)
    // ============================================================================

    @PostMapping("/restaurar")
    @ResponseStatus(HttpStatus.OK)
    public void restaurarSistemaAFechaYHora(
            @RequestParam() @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora
    ) {
        restauracionService.restaurarSistemaAFechaYHora(fechaHora);
    }
}
