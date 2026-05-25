package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionYaExisteException;
import ar.edu.unq.epersgeist.persistencia.neo.UbicacionDAONeo4j;
import ar.edu.unq.epersgeist.persistencia.neo.entity.UbicacionNeo4j;
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
        if (ubicacionDAOSQL.existsByName(ubicacion.getNombre())) {
            throw new UbicacionYaExisteException(ubicacion.getNombre());
        }

        ubicacionDAOSQL.save(ubicacion); //se actualiza el estado de ubicacion con el id
        ubicacionDAONeo.save(new UbicacionNeo4j(ubicacion)); //lo persisto en neo4j con dicho id
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
            ubicacionDAONeo.deleteById(id);
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
    // CONSULTAS DE CONEXIONES ENTRE UBICACIONES
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
        //recuperamos para pasar por la validación y tirar excepción si no están en la DB
        return ubicacionDAONeo.estanConectadas(idOrigen, idDestino);
    }

    // --------------------------------------------------------------------------------
}
