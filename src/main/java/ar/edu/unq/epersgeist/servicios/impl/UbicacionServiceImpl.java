package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.Ubicacion.UbicacionNeo4j;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionYaExisteException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionesNoConectadasException;
import ar.edu.unq.epersgeist.persistencia.neo.UbicacionDAONeo4j;
import ar.edu.unq.epersgeist.persistencia.sql.UbicacionDAOSQL;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// --------------------------------------------------------------------------------

@Service
@Transactional
@AllArgsConstructor

// --------------------------------------------------------------------------------

public class UbicacionServiceImpl implements UbicacionService {
    private final UbicacionDAOSQL ubicacionDAOSQL;
    private final UbicacionDAONeo4j ubicacionDAONeo;

    // --------------------------------------------------------------------------------
    // CREAR
    // --------------------------------------------------------------------------------

    @Override
    public Ubicacion crear(Ubicacion ubicacion) {
        // Busco si ya existe una ubicación con el mismo nombre
        Ubicacion existente = ubicacionDAOSQL.findByNombre(ubicacion.getNombre());

        // Si existe y NO está eliminada lógicamente, entonces no se puede crear otra con el mismo nombre
        if (existente != null && !existente.getDeletedAt()) {
            throw new UbicacionYaExisteException(ubicacion.getNombre());
        }

        // Si existe pero estaba soft deleted, la elimino físicamente para liberar el unique del nombre
        if (existente != null) {
            ubicacionDAOSQL.delete(existente);
            ubicacionDAOSQL.flush(); // Ejecuta el DELETE antes del INSERT
        }

        // Guardo la ubicación en SQL
        ubicacionDAOSQL.save(ubicacion);

        // La persisto también en Neo4j usando el id generado en SQL
        ubicacionDAONeo.save(new UbicacionNeo4j(ubicacion));

        return ubicacion;
    }

    // --------------------------------------------------------------------------------
    // RECUPERAR
    // --------------------------------------------------------------------------------

    @Override
    public Ubicacion recuperar(Long ubicacionId) {
        return ubicacionDAOSQL
                .findById(ubicacionId)
                .orElseThrow(() -> new UbicacionNoEncontradaException(ubicacionId));
    }

    @Override
    public List<Ubicacion> recuperarTodos() {
        return ubicacionDAOSQL.findAll();
    }

    // --------------------------------------------------------------------------------
    // ACTUALIZAR
    // --------------------------------------------------------------------------------

    @Override
    public void actualizar(Ubicacion ubicacion) {
        Long id = ubicacion.getId();

        if (id == null || !ubicacionDAOSQL.existsById(id)) {
            throw new UbicacionNoEncontradaException(id);
        }

        ubicacionDAOSQL.save(ubicacion);

        ubicacionDAONeo.actualizar(ubicacion.getId(), ubicacion.getNombre()); //necesito de op especial del dao de neo4j, porque lo tengo acá es un Ubicacion y no un UbicacionNeo4j!!
    }

    // --------------------------------------------------------------------------------
    // ELIMINAR
    // --------------------------------------------------------------------------------

    @Override
    public void eliminar(Long id) {
        ubicacionDAOSQL.findById(id).ifPresent(ubicacion -> {
            ubicacion.setDeletedAt(true);
            ubicacionDAOSQL.save(ubicacion);
            ubicacionDAONeo.deleteById(id); //hace un DETACH DELETE del nodo en la DB Neo4j con dicho id
        });
    }

    @Override
    public void eliminarTodos() {
        ubicacionDAOSQL.deleteAll();
        ubicacionDAONeo.deleteAll();
    }

    // --------------------------------------------------------------------------------
    // CONSULTAS DE RELACIONES
    // --------------------------------------------------------------------------------

    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        return ubicacionDAOSQL.espiritusEn(ubicacionId);
    }

    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId) {
        return ubicacionDAOSQL.mediumsSinEspiritusEn(ubicacionId);
    }

    // --------------------------------------------------------------------------------
    // MÉTODOS DE CONEXIONES ENTRE UBICACIONES
    // --------------------------------------------------------------------------------

    @Override
    public void conectar(Long idOrigen, Long idDestino, Long costo) {
        this.recuperar(idOrigen);
        this.recuperar(idDestino);

        ubicacionDAONeo.conectar(idOrigen, idDestino, costo);
    }

    @Override
    public Boolean estanConectadas(Long idOrigen, Long idDestino) {
        this.recuperar(idOrigen);
        this.recuperar(idDestino);

        return ubicacionDAONeo.estanConectadas(idOrigen, idDestino);
    }

    @Override
    public List<Ubicacion> caminoMasRentable(Long idOrigen, Long idDestino) {
        Ubicacion origen = this.recuperar(idOrigen);
        this.recuperar(idDestino);

        //caso borde (si origen y destino son la misma Ubicacion)
        if (idOrigen.equals(idDestino)) {
            return List.of(origen);
        }

        List<UbicacionNeo4j> ubis = ubicacionDAONeo.caminoMasRentable(idOrigen, idDestino);

        if(ubis.isEmpty()) {
            throw new UbicacionesNoConectadasException(idOrigen, idDestino);
        }

        return ubis.stream().map((u) -> this.recuperar(u.getId())).toList();
    }

    // --------------------------------------------------------------------------------
    // CAMINO MÁS CORTO
    // --------------------------------------------------------------------------------

    @Override
    public List<Ubicacion> caminoMasCorto(Long idOrigen, Long idDestino) {
        // 1. Valido que existan ambas ubicaciones
        Ubicacion origen = recuperar(idOrigen);
        recuperar(idDestino);

        // 2. Caso borde: mismo nodo (shortestPath no soporta origen = destino)
        if (idOrigen.equals(idDestino)) {
            return List.of(origen);
        }

        // 3. Obtengo el camino más corto en Neo4j
        List<UbicacionNeo4j> caminoNeo = ubicacionDAONeo.caminoMasCorto(idOrigen, idDestino);

        // 4. Si no hay camino, tiro excepción
        if (caminoNeo.isEmpty()) {
            throw new UbicacionesNoConectadasException(idOrigen, idDestino);
        }

        // 5. Convierto las ubicaciones de Neo4j a las de SQL (para devolver el mismo tipo que el servicio)
        return caminoNeo.stream()
                .map(u -> recuperar(u.getId()))
                .toList();
    }

    // --------------------------------------------------------------------------------
}
