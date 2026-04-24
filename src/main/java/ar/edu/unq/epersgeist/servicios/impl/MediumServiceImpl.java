package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDeEspirituNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDeMediumNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.exceptions.ExorcismoPropioNoPermitidoException;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;

import java.util.ArrayList;
import java.util.List;


public class MediumServiceImpl implements MediumService {
    private MediumDAO mediumDao;
    private EspirituDAO espirituDao; //se requiere para usar en el invocar()

    public MediumServiceImpl(MediumDAO mediumDao, EspirituDAO espirituDao) {
        this.mediumDao = mediumDao;
        this.espirituDao = espirituDao;
    }

    @Override
    public Medium crear(Medium medium) {
        return HibernateTransactionRunner.runTrx(() -> this.mediumDao.crear(medium));
    }

    @Override
    public Medium recuperar(Long idMedium) {
        return HibernateTransactionRunner.runTrx(() -> {
            Medium mediumRecuperado = mediumDao.recuperar(idMedium);

            if (mediumRecuperado == null) {
                throw new ErrorDeMediumNoEncontradoException(idMedium);
            }

            return mediumRecuperado;
        });
    }

    @Override
    public void actualizar(Medium medium) {
        HibernateTransactionRunner.runTrx(() -> {
            this.mediumDao.actualizar(medium);
            return null;
        });
    }

    @Override
    public void eliminar(Long idMedium) {
        HibernateTransactionRunner.runTrx(() -> {
            mediumDao.eliminar(idMedium);
            return null;
        });
    }

    @Override
    public List<Medium> recuperarTodos() {
        return HibernateTransactionRunner.runTrx(() ->
                mediumDao.recuperarTodos());
    }

    @Override
    public void descansar(Long id) {
        HibernateTransactionRunner.runTrx(() -> {
            Medium m = mediumDao.recuperar(id);

            if (m == null) {
                throw new ErrorDeMediumNoEncontradoException(id);
            }

            m.descansar();
            return null;
        });
    }


    @Override
    public Espiritu invocar(Long mediumId, Long espirituId) {
        return HibernateTransactionRunner.runTrx(() -> {
            Medium m = mediumDao.recuperar(mediumId);
            Espiritu e = espirituDao.recuperar(espirituId);

            if (m == null) {
                throw new ErrorDeMediumNoEncontradoException(mediumId);
            }
            if (e == null) {
                throw new ErrorDeEspirituNoEncontradoException(espirituId);
            }

            return m.invocar(e);

        });
    }


    @Override
    public void exorcizar(Long idMediumExorcista, Long idMediumAExorcizar) throws ExorcismoPropioNoPermitidoException {
        if (idMediumExorcista == idMediumAExorcizar) {
            throw new ExorcismoPropioNoPermitidoException("Un medium no puede exorcizarse a si mismo");
        }
        HibernateTransactionRunner.runTrx(() -> {
            Medium mediumAtacante = mediumDao.recuperar(idMediumExorcista);
            Medium mediumAExorcizar = mediumDao.recuperar(idMediumAExorcizar);
            // aplico logica de modelo
            mediumAtacante.exorcizar(mediumAExorcizar);
            // persisto los cambios (opcional, ya que el session.flush() del final de la transacción ya se encarga de persistir los cambios de estado)
            // mediumDao.actualizar(mediumAtacante);
            // mediumDao.actualizar(mediumAExorcizar);

            return null;

        });

    }


    @Override
    public List<Espiritu> espiritus(Long mediumId) {
        return HibernateTransactionRunner.runTrx(() -> {
            Medium recuperado = this.recuperar(mediumId);
            return new ArrayList<Espiritu>(recuperado.getEspiritus());
        });
    }


    @Override
    public void eliminarTodos() {
        HibernateTransactionRunner.runTrx(() -> {

            mediumDao.eliminarTodos();
            return null;
        });
    }


}
