package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.*;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UbicacionServiceImpl implements UbicacionService {
    private final UbicacionDAO ubicacionDAO;

    // --------------------------------------------------------------------------------

    @Override
    public Ubicacion crear(Ubicacion ubicacion) {
        String nombre = ubicacion.getNombre();

        if (ubicacionDAO.existsByName(nombre)) {
            throw new UbicacionYaExisteConEseNombreException(nombre);
        }

        return ubicacionDAO.save(ubicacion);
    }

    // --------------------------------------------------------------------------------

    @Override
    public Ubicacion recuperar(Long idUbicacion) {
        return ubicacionDAO
                .findById(idUbicacion)
                .orElseThrow(() -> new ErrorDeUbicacionNoEncontradaException(idUbicacion)
        );
    }

    @Override
    public List<Ubicacion> recuperarTodos() {
        return ubicacionDAO.findAll();
    }

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

    @Override
    public void eliminar(Long id) {
        ubicacionDAO.deleteById(id);
    }

    @Override
    public void eliminarTodos() {
        ubicacionDAO.deleteAll();
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        return ubicacionDAO.espiritusEn(ubicacionId);
    }

    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId) {
        return ubicacionDAO.mediumsSinEspiritusEn(ubicacionId);
    }
}
