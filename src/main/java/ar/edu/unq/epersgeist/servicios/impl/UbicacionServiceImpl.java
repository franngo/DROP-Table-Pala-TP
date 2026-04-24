package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.*;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;

import java.util.List;

public class UbicacionServiceImpl implements UbicacionService {
    private final UbicacionDAO ubicacionDAO;

    public UbicacionServiceImpl(UbicacionDAO ubicacionDAO) {
        this.ubicacionDAO = ubicacionDAO;
    }

    // --------------------------------------------------------------------------------

    @Override
    public Ubicacion crear(String nombre) {
        if (nombre == null) {
            throw new NombreUbicacionInvalidoException();
        }

        nombre = nombre.trim();

        if (nombre.isEmpty()) {
            throw new NombreUbicacionInvalidoException();
        }

        if (this.ubicacionDAO.recuperarPorNombre(nombre) != null) {
            throw new UbicacionYaExisteException(nombre);
        }

        return this.ubicacionDAO.guardar(new Ubicacion(nombre));
    }

    // --------------------------------------------------------------------------------

    @Override
    public Ubicacion recuperar(Long id) {
        if (id == null) {
            throw new IdUbicacionInvalidoException();
        }

        return this.ubicacionDAO.recuperar(id);
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<Ubicacion> recuperarTodos() {
        return ubicacionDAO.recuperarTodos();
    }

    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        if (ubicacionId == null) {
            throw new IdUbicacionInvalidoException();
        }

        Ubicacion ubicacion = this.ubicacionDAO.recuperar(ubicacionId);

        if (ubicacion == null) {
            throw new UbicacionNoEncontradaException(ubicacionId);
        }

        return ubicacion.getEspiritus();
    }

    @Override
    public void actualizar(Ubicacion ubicacionMod) {
        if (ubicacionMod == null || ubicacionMod.getId() == null) {
            throw new ErrorAlActualizarUbicacionException("No se puede actualizar una ubicacion que no tiene id.");
        }

        Ubicacion existente = ubicacionDAO.recuperar(ubicacionMod.getId());

        if (existente == null) {
            throw new ErrorAlActualizarUbicacionException(
                    "No existe en la BD la ubicacion con id: " + ubicacionMod.getId()
            );
        }

        this.ubicacionDAO.actualizar(ubicacionMod);
        return;
    }

    @Override
    public void eliminar(Long id) {
        HibernateTransactionRunner.runTrx(() -> {
            ubicacionDAO.eliminar(id);
            return null;
        });
    }

    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId) {
        if (ubicacionId == null) {
            throw new IdUbicacionInvalidoException();
        }

        Ubicacion ubicacion = this.ubicacionDAO.recuperar(ubicacionId);

        if (ubicacion == null) {
            throw new UbicacionNoEncontradaException(ubicacionId);
        }

        return ubicacion.getMediums().stream()
                .filter(medium -> medium.getEspiritus().isEmpty())
                .toList();
    }

    // --------------------------------------------------------------------------------
}
