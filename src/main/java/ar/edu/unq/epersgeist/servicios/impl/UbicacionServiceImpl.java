package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionYaExisteException;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
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
    private final UbicacionDAO ubicacionDAO;

    // --------------------------------------------------------------------------------
    // CREAR
    // --------------------------------------------------------------------------------

    @Override
    public Ubicacion crear(Ubicacion ubicacion) {
        if (ubicacionDAO.existsByName(ubicacion.getNombre())) {
            throw new UbicacionYaExisteException(ubicacion.getNombre());
        }

        return ubicacionDAO.save(ubicacion);
    }

    // --------------------------------------------------------------------------------
    // RECUPERAR
    // --------------------------------------------------------------------------------

    @Override
    public Ubicacion recuperar(Long ubicacionId) {
        return ubicacionDAO
                .findById(ubicacionId)
                .orElseThrow(() -> new UbicacionNoEncontradaException(ubicacionId));
    }

    @Override
    public List<Ubicacion> recuperarTodos() {
        return ubicacionDAO.findAll();
    }

    // --------------------------------------------------------------------------------
    // ACTUALIZAR
    // --------------------------------------------------------------------------------

    @Override
    public void actualizar(Ubicacion ubicacion) {
        Long id = ubicacion.getId();

        if (id == null || !ubicacionDAO.existsById(id)) {
            throw new UbicacionNoEncontradaException(id);
        }

        ubicacionDAO.save(ubicacion);
    }

    // --------------------------------------------------------------------------------
    // ELIMINAR
    // --------------------------------------------------------------------------------

    @Override
    public void eliminar(Long id) {
        ubicacionDAO.findById(id).ifPresent(ubicacion -> {
            ubicacion.setDeletedAt(true);
            ubicacionDAO.save(ubicacion);
        });
    }

    @Override
    public void eliminarTodos() {
        ubicacionDAO.deleteAll();
    }

    // --------------------------------------------------------------------------------
    // CONSULTAS DE RELACIONES
    // --------------------------------------------------------------------------------

    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        return ubicacionDAO.espiritusEn(ubicacionId);
    }

    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId) {
        return ubicacionDAO.mediumsSinEspiritusEn(ubicacionId);
    }

    // --------------------------------------------------------------------------------
}
