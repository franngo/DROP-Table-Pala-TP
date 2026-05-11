package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EspirituService {
    Espiritu crear(Espiritu espiritu);

    Espiritu recuperar(Long espirituId);

    void actualizar(Espiritu espiritu);

    void eliminar(Long espirituId);

    List<Espiritu> recuperarTodos();

    Medium conectar(Long espirituId, Long mediumId);

    List<Demonio> espiritusDemoniacos();

    void eliminarTodos();

    Page<Demonio> espiritusDemoniacos(Direccion direccion, int pag, int cantPorPag);


}
