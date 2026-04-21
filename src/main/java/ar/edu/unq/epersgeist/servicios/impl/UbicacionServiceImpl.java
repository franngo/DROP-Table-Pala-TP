package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.IdUbicacionInvalidoException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.NombreUbicacionInvalidoException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionYaExisteException;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.UbicacionService;

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

    // --------------------------------------------------------------------------------

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

    // --------------------------------------------------------------------------------
}
