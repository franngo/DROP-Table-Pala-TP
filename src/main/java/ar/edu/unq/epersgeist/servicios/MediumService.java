package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.exceptions.ExorcismoPropioNoPermitidoException;

import java.util.List;

public interface MediumService {
    Medium crear(Medium medium);

    Medium recuperar(Long idMedium);

    void actualizar(Medium medium);

    void eliminar(Long idMedium);

    List<Medium> recuperarTodos();

    void descansar(Long id);

    List<Medium> espiritus();

    Espiritu invocar(Long mediumId, Long espirituId);

    void eliminarTodos();


    void exorcizar(Long idMediumExorcista, Long idMediumAExorcizar) throws ExorcismoPropioNoPermitidoException;

    List<Espiritu> espiritus(Long mediumId);


}
