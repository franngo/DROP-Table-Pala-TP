package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.UbicacionMongo;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.CoordenadaNoPerteneceAUbicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionesNoConectadasException;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.Ubicacion.UbicacionNeo4j;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.medium.MediumMongo;
import ar.edu.unq.epersgeist.persistencia.mongo.EspirituDAOMongo;
import ar.edu.unq.epersgeist.persistencia.mongo.MediumDAOMongo;
import ar.edu.unq.epersgeist.persistencia.mongo.UbicacionDAOMongo;
import ar.edu.unq.epersgeist.persistencia.neo.UbicacionDAONeo4j;
import ar.edu.unq.epersgeist.persistencia.raven.RegistroHistoricoDAO;
import ar.edu.unq.epersgeist.persistencia.sql.EspirituDAOSQL;
import ar.edu.unq.epersgeist.persistencia.sql.MediumDAOSQL;
import ar.edu.unq.epersgeist.persistencia.sql.UbicacionDAOSQL;
import ar.edu.unq.epersgeist.servicios.MediumService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@AllArgsConstructor
public class MediumServiceImpl implements MediumService {
    private final MediumDAOSQL mediumDAOSQL;
    private final MediumDAOMongo mediumDAOMongo;
    private final EspirituDAOSQL espirituDAOSQL;
    private final EspirituDAOMongo espirituDAOMongo;;
    private final UbicacionDAOSQL ubicacionDAOSQL;
    private final UbicacionDAONeo4j ubicacionDAONeo4j;
    private final UbicacionDAOMongo ubicacionDAOMongo;
    private RegistroHistoricoDAO registroHistorico;

    @Override
    public Medium crear(Medium medium, Coordenada coordenada) {
        if (medium == null) {
            throw new ErrorAlCrearMediumException("medium nulo.");
        }

        if (medium.getId() != null) {
            throw new ErrorAlCrearMediumException("ya tiene un id asignado.");
        }

        //Se chequea que la coordenada dada pertenezca al área de la Ubicación del Medium
        List<UbicacionMongo> ubiCord = ubicacionDAOMongo.buscarUbicacionQueContiene(coordenada.getLatitud(), coordenada.getLongitud());
        if(ubiCord.isEmpty() || !Objects.equals(ubiCord.getFirst().getId(), medium.getUbicacion().getId())) {
            throw new CoordenadaNoPerteneceAUbicacion();
        }

        mediumDAOSQL.save(medium);

        MediumMongo mediumMongo = mediumDAOMongo.save(new MediumMongo(medium, coordenada));

        registroHistorico.registrarEstado("mediums_sql/" + medium.getId(), medium);
        registroHistorico.registrarEstado("mediums_mongo/" + medium.getId(), mediumMongo);

        return medium;
    }

    @Override
    public Medium recuperar(Long idMedium) {

        return mediumDAOSQL.findById(idMedium).orElseThrow(() -> new ErrorDeMediumNoEncontradoException(idMedium));
    }

    @Override
    public void actualizar(Medium medium) {
        if (medium.getId() == null) {
            throw new ErrorAlActualizarMediumSinIdException("No se puede actualizar un medium que no tiene id.");
        }

        //lo intento recuperar solo para saber si está o no en la DB y si debo tirar el error
        Long idMedium = medium.getId();
        mediumDAOSQL.findById(idMedium).orElseThrow(() -> new ErrorAlActualizarMediumException("No existe en la BD el medium con id: " + idMedium));

        this.mediumDAOSQL.save(medium);

        registroHistorico.registrarEstado("mediums_sql/" + medium.getId(), medium);
    }

    @Override
    public void eliminar(Long idMedium) {
        Medium medium = this.recuperar(idMedium);
        medium.setDeletedAt(true);
        mediumDAOSQL.save(medium);
        mediumDAOMongo.deleteById(idMedium);

        registroHistorico.registrarEstado("mediums_sql/" + medium.getId(), medium);
        //borrar en RavenDB el doc mediumMongo
    }

    @Override
    public List<Medium> recuperarTodos() {
        return mediumDAOSQL.findAll();
    }

    @Override
    public void descansar(Long idMedium) {
        Medium m = this.recuperar(idMedium);
        m.descansar();
    }

    @Override
    public Espiritu invocar(Long mediumId, Long espirituId) {

        Medium medium = this.recuperar(mediumId);

        Espiritu espiritu = espirituDAOSQL
                .findById(espirituId)
                .orElseThrow(
                        () -> new ErrorDeEspirituNoEncontradoException(espirituId)
                );

        MediumMongo mediumMongo = mediumDAOMongo
                .findById(mediumId)
                .orElseThrow(
                        () -> new ErrorDeMediumNoEncontradoException(mediumId)
                );

        GeoJsonPoint coordenadasMedium = mediumMongo.getCoordenada();

        double longitud = coordenadasMedium.getX();
        double latitud = coordenadasMedium.getY();

        double distancia =
                espirituDAOMongo
                        .distanciaHastaEspiritu(longitud, latitud, espirituId)
                        .distanciaCalculada();

        // 50 KM EN METROS
        if (distancia > 50000) {
            throw new EspirituMuyLejanoException();
        }

        return medium.invocar(espiritu);
    }

    @Override
    public void exorcizar(Long idMediumExorcista, Long idMediumAExorcizar) throws ExorcismoPropioNoPermitidoException {
        if (idMediumExorcista.equals(idMediumAExorcizar)) {
            throw new ExorcismoPropioNoPermitidoException("Un medium no puede exorcizarse a si mismo");
        }

        Medium mediumAtacante = this.recuperar(idMediumExorcista);
        Medium mediumAExorcizar = this.recuperar(idMediumAExorcizar);

        //en el service porque involucra ids, que es lo que usamos para comparar objetos por igualdad. Los recuperar anteriores nos aseguran los ids

        if (!mediumAtacante.getUbicacion().getId().equals(mediumAExorcizar.getUbicacion().getId())) {
            throw new ErrorDeMediumExorcizandoAOtroEnDistintaUbicacion(mediumAtacante.getId(), mediumAExorcizar.getId());
        }

        mediumAtacante.exorcizar(mediumAExorcizar);
    }


    @Override
    public List<Espiritu> espiritus(Long mediumId) {
        Medium recuperado = this.recuperar(mediumId);
        List<Espiritu> espiritus = recuperado.getEspiritus();
        espiritus.size();
        return espiritus;
    }

    @Override
    public void mover(Long mediumId, Double latitud, Double longitud) {


        Medium mediumAMover = mediumDAOSQL.findById(mediumId)
                .orElseThrow(() ->
                        new ErrorDeMediumNoEncontradoException(mediumId)
                );

        // Busco en ubicacion mongo si existe una ubicacion dada.
        List<UbicacionMongo> ubicaciones = ubicacionDAOMongo.buscarUbicacionQueContiene(latitud, longitud);
        if (ubicaciones.isEmpty()) {
            throw new CoordenadasSinUbicacion();
        }

        Long idDestino = ubicaciones.getFirst().getId();

        // Ubicacion neo para calcular si existe a donde se puede mover
        UbicacionNeo4j ubicacionNeoDestino =
                ubicacionDAONeo4j.findById(idDestino)
                        .orElseThrow(() ->
                                new UbicacionNoEncontradaException(idDestino)
                        );
        Ubicacion ubicacionActualMedium = mediumAMover.getUbicacion();
        Long ubicacionMediumId = ubicacionActualMedium.getId();

        if (!quiereMoverseALaMisma(ubicacionMediumId, idDestino)) {

            // Procedo a recuperar la ubicacion destino de la fuente de la verdad
            Ubicacion ubicacionDestino = ubicacionDAOSQL.findById(idDestino)
                    .orElseThrow(() ->
                            new UbicacionNoEncontradaException(idDestino));

            // Se valida si las ubicaciones estan conectadas
            this.validarConexion(ubicacionMediumId, ubicacionNeoDestino.getId());

            // Calculo si esta a menos de 30km de distacia
            mediumDAOMongo.mediumDentroDeDistancia(mediumId, latitud, longitud, 30000.0)
                    .orElseThrow(()->
                            new UbicacionLejanaException("La ubicacion esta a mas de 30km"));

            // calculo el costo de moverse
            int costo = Math.toIntExact(ubicacionDAONeo4j.costoHacia(ubicacionMediumId, idDestino));

            // el medium se mueve
            mediumAMover.moverA(ubicacionDestino, costo);


            // actualizo en mongo
            MediumMongo mediumMongo =
                    mediumDAOMongo.findById(mediumId)
                            .orElseThrow();

            mediumMongo.setCoordenada(
                    new GeoJsonPoint(longitud, latitud)
            );

            mediumAMover.getEspiritus()
                    .forEach(e ->
                            espirituDAOMongo.actualizarCoordenada(
                                    e.getId(),
                                    longitud,
                                    latitud
                            )
                    );

            // depende el estado del medium se guarda o se elimina
            if (mediumAMover.sinMana()) {
                this.eliminar(mediumAMover.getId());
                mediumDAOMongo.deleteById(mediumAMover.getId());
            } else {
                mediumDAOSQL.save(mediumAMover);
                mediumDAOMongo.actualizarCoordenada(mediumId, longitud, latitud);
            }
        }
    }

    private void validarConexion(Long idOrigen, Long idDestino) {
        if (!ubicacionDAONeo4j.estanConectadas(idOrigen, idDestino)) {
            throw new UbicacionesNoConectadasException(idOrigen, idDestino);
        }
    }


    private boolean quiereMoverseALaMisma(Long idUbicacionActual, Long idUbicacionAMover) {
        return Objects.equals(idUbicacionActual, idUbicacionAMover);
    }

    @Override
    public void eliminarTodos() {
        mediumDAOSQL.deleteAll();
        mediumDAOMongo.deleteAll();
    }

}
