package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.Ubicacion.UbicacionNeo4j;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.modelo.medium.MediumMongo;
import ar.edu.unq.epersgeist.persistencia.mongo.MediumDAOMongo;
import ar.edu.unq.epersgeist.persistencia.neo.UbicacionDAONeo4j;
import ar.edu.unq.epersgeist.persistencia.sql.EspirituDAOSQL;
import ar.edu.unq.epersgeist.persistencia.sql.MediumDAOSQL;
import ar.edu.unq.epersgeist.persistencia.sql.UbicacionDAOSQL;
import ar.edu.unq.epersgeist.servicios.MediumService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class MediumServiceImpl implements MediumService {
    private final MediumDAOSQL mediumDAOSQL;
    private final MediumDAOMongo mediumDAOMongo;
    private final EspirituDAOSQL espirituDAOSQL; //se requiere para usar en el invocar()
    private final UbicacionDAOSQL ubicacionDAOSQL;
    private final UbicacionDAONeo4j ubicacionDAONeo4j;

    public MediumServiceImpl(MediumDAOSQL mediumDAOSQL, EspirituDAOSQL espirituDAOSQL, UbicacionDAOSQL ubicacionDAOSQL, UbicacionDAONeo4j ubicacionDAONeo4j, MediumDAOMongo mediumDAOMongo) {
        this.mediumDAOSQL = mediumDAOSQL;
        this.mediumDAOMongo = mediumDAOMongo;
        this.espirituDAOSQL = espirituDAOSQL;
        this.ubicacionDAOSQL = ubicacionDAOSQL;
        this.ubicacionDAONeo4j = ubicacionDAONeo4j;
    }

    @Override
    public Medium crear(Medium medium, Coordenada coordenada) {
        if (medium == null) {
            throw new ErrorAlCrearMediumException("medium nulo.");
        }

        if (medium.getId() != null) {
            throw new ErrorAlCrearMediumException("ya tiene un id asignado.");
        }

        mediumDAOSQL.save(medium);

        mediumDAOMongo.save(new MediumMongo(medium, coordenada));

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
    }

    @Override
    public void eliminar(Long idMedium) {
        Medium medium = this.recuperar(idMedium);
        medium.setDeletedAt(true);
        mediumDAOSQL.save(medium);
        mediumDAOMongo.deleteById(idMedium);
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
        Medium m = this.recuperar(mediumId);

        Espiritu e = espirituDAOSQL.findById(espirituId).orElseThrow(() -> new ErrorDeEspirituNoEncontradoException(espirituId));

        return m.invocar(e);
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
    public void mover(Long mediumId, Long ubicacionId) {

        Medium mediumAMover = mediumDAOSQL.findById(mediumId)
                .orElseThrow(() ->
                        new ErrorDeMediumNoEncontradoException(mediumId)
                );

        // Ubicacion neo para calcular si existe a donde se puede mover
        UbicacionNeo4j ubicacionAMover = ubicacionDAONeo4j.findById(ubicacionId)
                .orElseThrow(() ->
                        new UbicacionNoEncontradaException(ubicacionId)
                );
        Ubicacion ubicacionActualMedium = mediumAMover.getUbicacion();
        Long ubicacionMediumId = ubicacionActualMedium.getId();
        Long ubicacionAIrId = ubicacionAMover.getId();

        if (!quiereMoverseALaMisma(ubicacionActualMedium.getId(), ubicacionAIrId)) {

            // Procedo a recuperar la ubicacion destino de la fuente de la verdad
            Ubicacion ubicacionDestino = ubicacionDAOSQL.findById(ubicacionId)
                    .orElseThrow(() ->
                            new UbicacionNoEncontradaException(ubicacionId));

            // Se valida si las ubicaciones estan conectadas
            this.validarConexion(ubicacionActualMedium, ubicacionAMover);

            // calculo el costo de moverse
            int costo = Math.toIntExact(ubicacionDAONeo4j.costoHacia(ubicacionMediumId, ubicacionAIrId));

            // el medium se mueve
            mediumAMover.moverA(ubicacionDestino, costo);

            // depende el estado del medium se guarda o se elimina
            if (mediumAMover.sinMana()) {
                this.eliminar(mediumAMover.getId());
            } else {
                mediumDAOSQL.save(mediumAMover);
            }

        }
    }

    private void validarConexion(Ubicacion ubicacion, UbicacionNeo4j ubicacionAMover) {
        if (!ubicacionDAONeo4j.estanConectadas(ubicacion.getId(), ubicacionAMover.getId())) {
            throw new UbicacionLejanaException("La ubicacion esta lejana");
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
