package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import ar.edu.unq.epersgeist.modelo.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Espiritu.EspirituMongo;
import ar.edu.unq.epersgeist.modelo.Ubicacion.UbicacionMongo;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.CoordenadaNoPerteneceAUbicacion;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.persistencia.mongo.EspirituDAOMongo;
import ar.edu.unq.epersgeist.persistencia.mongo.UbicacionDAOMongo;
import ar.edu.unq.epersgeist.persistencia.raven.RegistroHistoricoDAO;
import ar.edu.unq.epersgeist.persistencia.sql.EspirituDAOSQL;
import ar.edu.unq.epersgeist.persistencia.sql.MediumDAOSQL;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class EspirituServiceImpl implements EspirituService {
    private EspirituDAOSQL espirituDAOSQL;

    private EspirituDAOMongo espirituDAOMongo;

    private MediumDAOSQL mediumDAOSQL;

    private UbicacionDAOMongo ubicacionDAOMongo;

    @Autowired
    private RegistroHistoricoDAO registroHistorico;

    @Override
    public Espiritu crear(Espiritu espiritu, Coordenada coordenada) {
        this.validarEspiritu(espiritu);

        // Se chequea que la coordenada dada pertenezca al área de la Ubicación del Espiritu
        List<UbicacionMongo> ubiCord = ubicacionDAOMongo.buscarUbicacionQueContiene(coordenada.getLatitud(), coordenada.getLongitud());
        if (ubiCord.isEmpty() || !Objects.equals(ubiCord.getFirst().getId(), espiritu.getUbicacion().getId())) {
            throw new CoordenadaNoPerteneceAUbicacion();
        }

        espirituDAOSQL.save(espiritu);

        EspirituMongo espirituMongo = espirituDAOMongo.save(new EspirituMongo(espiritu, coordenada));

        // ----------------------------------------
        // REGISTRO HISTORICO
        // ----------------------------------------

        registroHistorico.registrarEstado("espiritus_sql/" + espiritu.getId(), espiritu);
        registroHistorico.registrarEstado("espiritus_mongo/" + espiritu.getId(), espirituMongo);

        return espiritu;
    }

    private void validarEspiritu(Espiritu espiritu) {
        if (espiritu == null) {
            throw new ErrorAlCrearEspirituException("No se puede crear el espiritu");
        }
        if (espiritu.getId() != null) {
            throw new ErrorAlCrearEspirituException("No se puede crear espiritu con id existente");
        }
    }


    @Override
    public Espiritu recuperar(Long espirituId) {

        return espirituDAOSQL.findById(espirituId).orElseThrow(
                () -> new ErrorDeEspirituNoEncontradoException(espirituId)
        );
    }

    @Override
    public void actualizar(Espiritu espiritu) {
        if (espiritu.getId() == null) {
            throw new ErrorAlActualizarEspirituSinIdException("No se puede actualizar un espiritu que no tiene id.");
        }

        Long espirituId = espiritu.getId();
        espirituDAOSQL.findById(espirituId).orElseThrow(() -> new ErrorAlActualizarEspirituException("No existe en la BD el espiritu con id: " + espirituId));

        this.espirituDAOSQL.save(espiritu);

        // ----------------------------------------
        // REGISTRO HISTORICO
        // ----------------------------------------

        espirituDAOMongo.findById(espirituId).ifPresent(espirituMongo -> {
            registroHistorico.registrarEstado("espiritus_mongo/" + espirituId, espirituMongo);
        });

        registroHistorico.registrarEstado("espiritus_sql/" + espiritu.getId(), espiritu);
    }

    @Override
    public void eliminar(Long espirituId) {

        Espiritu espiritu = this.recuperar(espirituId);
        espiritu.setDeletedAt(true);
        espirituDAOSQL.save(espiritu);

        // ----------------------------------------
        // REGISTRO HISTORICO
        // ----------------------------------------

        espirituDAOMongo.findById(espirituId).ifPresent(espirituMongo -> {
            registroHistorico.registrarEstado("espiritus_mongo/" + espirituId, espirituMongo);
        });

        espirituDAOMongo.deleteById(espirituId);

        registroHistorico.registrarEstado("espiritus_sql/" + espiritu.getId(), espiritu);
    }

    @Override
    public List<Espiritu> recuperarTodos() {

        return espirituDAOSQL.findAll();
    }

    @Override
    public Medium conectar(Long espirituId, Long mediumId) {
        Optional<Espiritu> espirituAConectar = espirituDAOSQL.findById(espirituId);

        Optional<Medium> mediumAConectar = mediumDAOSQL.findById(mediumId);

        if (espirituAConectar.isEmpty()) {
            throw new ErrorAlConectarEspirituConMedium("El espiritu no existe en la bbdd");
        }
        if (mediumAConectar.isEmpty()) {
            throw new ErrorAlConectarEspirituConMedium("El medium no existe en la bbdd");
        }
        Espiritu espirituEncontrado = espirituAConectar.get();
        Medium mediumEncontrado = mediumAConectar.get();

        // No puede conectarse si está siendo dominado
        if (espirituEncontrado.estaDominado()) {
            throw new EspirituDominadoException();
        }

        // Las validaciones sobre si está libre y en la misma ubicación se dan en el method del modelo.
        espirituEncontrado.aumentarConexionYConectar(mediumEncontrado);

        // ----------------------------------------
        // REGISTRO HISTORICO
        // ----------------------------------------

        registroHistorico.registrarEstado("espiritus_sql/" + espirituEncontrado.getId(), espirituEncontrado);

        return mediumDAOSQL.save(mediumEncontrado);
    }

    @Override
    public List<Demonio> espiritusDemoniacos() {

        return espirituDAOSQL.espiritusDemoniacos();
    }

    //versión que implementa la paginación
    @Override
    public Page<Espiritu> espiritusDemoniacos(Direccion direccion, int pag, int cantPorPag) {
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

        return espirituDAOSQL.espiritusDemoniacos(pageable);

    }

    @Override
    public void eliminarTodos() {

        espirituDAOSQL.deleteAll();
        espirituDAOMongo.deleteAll();

    }

    @Override
    public void dominar(Long espirituDominanteId, Long espirituADominarId) {
        Espiritu espirituDominante = this.recuperar(espirituDominanteId);
        Espiritu espirituADominar = this.recuperar(espirituADominarId);

        EspirituMongo dominanteMongo =
                espirituDAOMongo.findById(espirituDominanteId).orElseThrow(() -> new ErrorDeEspirituNoEncontradoException(espirituDominanteId));
        //Se evita la mala práctica de hacer get() directamente sobre un Optional, aunque, a menos que haya inconsistencias, no será vacío.

        GeoJsonPoint coordenadasDom = dominanteMongo.getCoordenada();

        double longitud = coordenadasDom.getX();
        double latitud = coordenadasDom.getY();

        double distanciaEspiritus = this.espirituDAOMongo.distanciaHastaEspiritu(longitud, latitud, espirituADominarId).distanciaCalculada();

        //Se realiza esta validación en service porque involucra ids y no tiene sentido en modelo.
        if (espirituDominante.estaDominado() && espirituADominar.getId().equals(espirituDominante.getEspirituDominante().getId())) {
            throw new ErrorDominacionInvalidaCandidatoLoDominaException(espirituDominante.getId(), espirituADominar.getId());
        }

        espirituDominante.dominar(espirituADominar, distanciaEspiritus);

        // ----------------------------------------
        // REGISTRO HISTORICO
        // ----------------------------------------

        registroHistorico.registrarEstado("espiritus_sql/" + espirituDominante.getId(), espirituDominante);
        registroHistorico.registrarEstado("espiritus_sql/" + espirituADominar.getId(), espirituADominar);
    }
}
