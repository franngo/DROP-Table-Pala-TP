package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.*;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionNoEncontradaException;
import ar.edu.unq.epersgeist.persistencia.neo.UbicacionDAONeo4j;
import ar.edu.unq.epersgeist.persistencia.neo.entity.UbicacionNeo4j;
import ar.edu.unq.epersgeist.persistencia.sql.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.sql.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.sql.UbicacionDAOSQL;
import ar.edu.unq.epersgeist.servicios.MediumService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class MediumServiceImpl implements MediumService {
    private final MediumDAO mediumDao;
    private final EspirituDAO espirituDao; //se requiere para usar en el invocar()
    private final UbicacionDAOSQL ubicacionDAOSQL;
    private final UbicacionDAONeo4j ubicacionDAONeo4j;

    public MediumServiceImpl(MediumDAO mediumDao, EspirituDAO espirituDao, UbicacionDAOSQL ubicacionDAOSQL, UbicacionDAONeo4j ubicacionDAONeo4j) {
        this.mediumDao = mediumDao;
        this.espirituDao = espirituDao;
        this.ubicacionDAOSQL = ubicacionDAOSQL;
        this.ubicacionDAONeo4j = ubicacionDAONeo4j;
    }

    @Override
    public Medium crear(Medium medium) {
        if (medium == null) {
            throw new ErrorAlCrearMediumException("medium nulo.");
        }

        if (medium.getId() != null) {
            throw new ErrorAlCrearMediumException("ya tiene un id asignado.");
        }

        return this.mediumDao.save(medium);
    }

    @Override
    public Medium recuperar(Long idMedium) {

        return mediumDao.findById(idMedium).orElseThrow(() -> new ErrorDeMediumNoEncontradoException(idMedium));
    }

    @Override
    public void actualizar(Medium medium) {
        if (medium.getId() == null) {
            throw new ErrorAlActualizarMediumSinIdException("No se puede actualizar un medium que no tiene id.");
        }

        //lo intento recuperar solo para saber si está o no en la DB y si debo tirar el error
        Long idMedium = medium.getId();
        mediumDao.findById(idMedium).orElseThrow(() -> new ErrorAlActualizarMediumException("No existe en la BD el medium con id: " + idMedium));

        this.mediumDao.save(medium);
    }

    @Override
    public void eliminar(Long idMedium) {
        Medium medium = this.recuperar(idMedium);
        medium.setDeletedAt(true);
        mediumDao.save(medium);
    }

    @Override
    public List<Medium> recuperarTodos() {
        return mediumDao.findAll();
    }

    @Override
    public void descansar(Long idMedium) {
        Medium m = this.recuperar(idMedium);
        m.descansar();
    }


    @Override
    public Espiritu invocar(Long mediumId, Long espirituId) {
        Medium m = this.recuperar(mediumId);

        Espiritu e = espirituDao.findById(espirituId).orElseThrow(() -> new ErrorDeEspirituNoEncontradoException(espirituId));

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

        Medium mediumAMover = mediumDao.findById(mediumId)
                .orElseThrow(() ->
                        new ErrorDeMediumNoEncontradoException(mediumId)
                );

        // Ubicacion neo para calcular si se puede mover
        UbicacionNeo4j ubicacionAMover = ubicacionDAONeo4j.findById(ubicacionId)
                .orElseThrow(() ->
                        new UbicacionNoEncontradaException(ubicacionId)
                );

        Long ubicacionMediumId = mediumAMover.getUbicacion().getId();
        Long ubicacionAIrId = ubicacionAMover.getId();

        if (!quiereMoverseALaMisma(ubicacionMediumId, ubicacionAIrId)) {

        // valido si estan conectadas
        this.validacionUbicacion(mediumAMover.getUbicacion(),ubicacionAMover);

        // calculo el costo de moverse
        double costo = ubicacionDAONeo4j.costoHacia(ubicacionMediumId,ubicacionAIrId);

        // ubicacion confirmada
        Ubicacion ubicacionConfirmada = ubicacionDAOSQL.findById(ubicacionId)
                    .orElseThrow(() ->
                            new UbicacionNoEncontradaException(ubicacionId));

            // En caso de que el medium se quede sin maná luego de moverse,
        // el mismo debe ser eliminado.

            mediumAMover.moverA(ubicacionConfirmada);
            this.actualizarEstadoMedium();
            mediumDao.save(mediumAMover);
        }
    }

    private void validacionUbicacion(Ubicacion ubicacion, UbicacionNeo4j ubicacionAMover) {
        if (!ubicacionDAONeo4j.estanConectadas(ubicacion.getId(),ubicacionAMover.getId()){
            throw new UbicaciónLejanaException("La ubicacion esta lejana");
        }
    }

    private boolean quiereMoverseALaMisma(Long idUbicacionActual, Long idUbicacionAMover) {
        return Objects.equals(idUbicacionActual, idUbicacionAMover);
    }

    @Override
    public void eliminarTodos() {
        mediumDao.deleteAll();
    }


}
