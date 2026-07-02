package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.persistencia.raven.RegistroHistoricoDAO;
import ar.edu.unq.epersgeist.servicios.RegistroHistoricoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class RegistroHistoricoServiceImpl implements RegistroHistoricoService {

    private final RegistroHistoricoDAO registroHistoricoDAO;

    public RegistroHistoricoServiceImpl(RegistroHistoricoDAO registroHistoricoDAO) {
        this.registroHistoricoDAO = registroHistoricoDAO;
    }

    // --------------------------------------------------------------------------------

    @Override
    public void registrarEstado(String ravenId, Object entidad) {
        registroHistoricoDAO.registrarEstado(ravenId, entidad);
    }

    // --------------------------------------------------------------------------------

    @Override
    public <T> List<T> obtenerHistorial(Class<T> claseEntidad, String ravenId) {
        return registroHistoricoDAO.obtenerHistorial(claseEntidad, ravenId);
    }

    // --------------------------------------------------------------------------------

    @Override
    public <T> T obtenerEstadoEnFecha(Class<T> claseEntidad, String ravenId, LocalDateTime fecha) {
        Date fechaDate = Date.from(fecha.atZone(ZoneId.systemDefault()).toInstant());
        return registroHistoricoDAO.obtenerEstadoEnFecha(claseEntidad, ravenId, fechaDate);
    }

    // --------------------------------------------------------------------------------

    @Override
    public void reiniciarBaseDeDatos() {
        registroHistoricoDAO.reiniciarBaseDeDatos();
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<String> obtenerTodosLosIdsDeColeccion(String nombreColeccion) {
        return registroHistoricoDAO.obtenerTodosLosIdsDeColeccion(nombreColeccion);
    }

}
