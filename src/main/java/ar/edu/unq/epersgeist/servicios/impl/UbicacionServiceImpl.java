package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.IdUbicacionInvalidoException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.NombreUbicacionInvalidoException;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionYaExisteException;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.UbicacionService;

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
}
