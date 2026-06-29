package ar.edu.unq.epersgeist.persistencia.raven;

public interface RegistroHistoricoDAO {
    void registrarEstado(String ravenId, Object entidad);

    <T> java.util.List<T> obtenerHistorial(Class<T> claseEntidad, String ravenId);

    <T> T obtenerEstadoEnFecha(Class<T> claseEntidad, String ravenId, java.util.Date fecha);

    void eliminarRegistroDePrueba(String ravenId);

    void borrarTodosLosDocumentos();
}
