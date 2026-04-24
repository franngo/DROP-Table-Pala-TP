package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.*;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;

import java.util.List;
import java.util.stream.Collectors;

public class UbicacionServiceImpl implements UbicacionService {
    private final UbicacionDAO ubicacionDAO;

    public UbicacionServiceImpl(UbicacionDAO ubicacionDAO) {
        this.ubicacionDAO = ubicacionDAO;
    }

    // --------------------------------------------------------------------------------

    @Override
    public Ubicacion crear(Ubicacion ubicacion) {
        return HibernateTransactionRunner.runTrx(() -> {
            if (ubicacion == null) {
                throw new UbicacionInvalidaException();
            }

            String nombre = ubicacion.getNombre();

            if (nombre == null || nombre.isBlank()) {
                throw new NombreUbicacionInvalidoException();
            }

            String trimmed = nombre.trim();
            ubicacion.setNombre(trimmed);

            if (this.ubicacionDAO.recuperarPorNombre(trimmed) != null) {
                throw new UbicacionYaExisteException(trimmed);
            }

            return this.ubicacionDAO.guardar(ubicacion);

        });
    }

    @Override
    public Ubicacion recuperar(Long id) {
        return HibernateTransactionRunner.runTrx(() -> {
            if (id == null) {
                throw new IdUbicacionInvalidoException();
            }

            return this.ubicacionDAO.recuperar(id);
        });
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<Ubicacion> recuperarTodos() {
        return HibernateTransactionRunner.runTrx(this.ubicacionDAO::recuperarTodos);
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        return HibernateTransactionRunner.runTrx(() -> {
            if (ubicacionId == null) {
                throw new IdUbicacionInvalidoException();
            }

            Ubicacion ubicacion = this.ubicacionDAO.recuperar(ubicacionId);

            if (ubicacion == null) {
                throw new UbicacionNoEncontradaException(ubicacionId);
            }

            return ubicacion.getEspiritus();
        });
    }

    // --------------------------------------------------------------------------------

    @Override
    public void actualizar(Ubicacion ubicacionMod) {
        HibernateTransactionRunner.runTrx(() -> {
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

            return null;
        });
    }

    // --------------------------------------------------------------------------------

    @Override
    public void eliminar(Long id) {
        HibernateTransactionRunner.runTrx(() -> {
            ubicacionDAO.eliminar(id);

            return null;
        });
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId) {
        return HibernateTransactionRunner.runTrx(() -> {
            if (ubicacionId == null) {
                throw new IdUbicacionInvalidoException();
            }

            Ubicacion ubicacion = this.ubicacionDAO.recuperar(ubicacionId);

            if (ubicacion == null) {
                throw new UbicacionNoEncontradaException(ubicacionId);
            }

            return ubicacionDAO.mediumsSinEspiritusEn(ubicacion);
        });
    }

    // --------------------------------------------------------------------------------
}
