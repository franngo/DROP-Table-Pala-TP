package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.List;

public interface MediumService {
    Medium crear(Medium medium);
    Medium recuperar(Long idMedium);
    void actualizar(Medium medium);
    void eliminar(Long idMedium);
    List<Medium> recuperarTodos();
    void descansar(Long idMedium);
    void exorcizar(Long idMediumExorcista, Long idMediumAExorcizar);
    List<Espiritu> espiritus(Long mediumId);
    Espiritu invocar(Long mediumId, Long espirituId);
}
