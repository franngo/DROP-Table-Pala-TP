package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionYaExisteException;
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

    // --------------------------------------------------------------------------------
    // CREAR
    // --------------------------------------------------------------------------------

    @Override
    public Ubicacion crear(Ubicacion ubicacion) {
        if (ubicacionDAOSQL.existsByName(ubicacion.getNombre())) {
            throw new UbicacionYaExisteException(ubicacion.getNombre());
        }

        return ubicacionDAOSQL.save(ubicacion);
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
    }

    // --------------------------------------------------------------------------------
    // ELIMINAR
    // --------------------------------------------------------------------------------

    @Override
    public void eliminar(Long id) {
        ubicacionDAOSQL.deleteById(id);
    }

    @Override
    public void eliminarTodos() {
        ubicacionDAOSQL.deleteAll();
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
}
