package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class EspirituServiceImpl implements EspirituService {
    private EspirituDAO espirituDao;

    private MediumDAO mediumDao;

    @Override
    public Espiritu crear(Espiritu espiritu) {
        return espirituDao.save(espiritu);
    }

    @Override
    public Espiritu recuperar(Long espirituId) {

        return espirituDao.findById(espirituId).orElseThrow(
                () -> new ErrorDeEspirituNoEncontradoException(espirituId)
        );
    }

    @Override
    public void actualizar(Espiritu espiritu) {

        espirituDao.save(espiritu);

    }

    @Override
    public void eliminar(Long espirituId) {

        espirituDao.deleteById(espirituId);

    }

    @Override
    public List<Espiritu> recuperarTodos() {

        return espirituDao.findAll();
    }

    @Override
    public Medium conectar(Long espirituId, Long mediumId) {
        Optional<Espiritu> espirituAConectar = espirituDao.findById(espirituId);

        Optional<Medium> mediumAConectar = mediumDao.findById(mediumId);

        if (espirituAConectar.isEmpty()) {
            throw new ErrorAlConectarEspirituConMedium("El espiritu no existe en la bbdd");
        }
        if (mediumAConectar.isEmpty()) {
            throw new ErrorAlConectarEspirituConMedium("El medium no existe en la bbdd");
        }
        Espiritu espirituEncontrado = espirituAConectar.get();
        Medium mediumEncontrado = mediumAConectar.get();
        //las validaciones sobre si está libre y en la misma ubicación se dan en el method del modelo.
        espirituEncontrado.aumentarConexionYConectar(mediumEncontrado);

        return mediumDao.save(mediumEncontrado);

    }

    @Override
    public List<Espiritu> espiritusDemoniacos() {

        return espirituDao.espiritusDemoniacos();
    }

    //versión que implementa la paginación
    @Override
    public List<Espiritu> espiritusDemoniacos(Direccion direccion, int pag, int cantPorPag) {
        if (direccion == null) {
            throw new ErrorDireccionNulaException();
        }

        if (pag <= 0) {
            throw new ErrorNumeroPaginaInvalidoExcepcion();
        }

        if (cantPorPag <= 0) {
            throw new ErrorCantidadPorPaginaInvalidaExcepcion();
        }

        Sort sort = (direccion == Direccion.DESCENDENTE)
                ? Sort.by("nivelDeConexion").descending()
                : Sort.by("nivelDeConexion").ascending();

        Pageable pageable = PageRequest.of(pag - 1, cantPorPag, sort);

        return espirituDao.espiritusDemoniacos(pageable).getContent();

    }

    @Override

    public void eliminarTodos() {

        espirituDao.deleteAll();

    }

}
