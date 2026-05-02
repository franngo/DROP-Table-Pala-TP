package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EspirituDAO {
    Espiritu guardar(Espiritu espiritu);

    Espiritu crear(Espiritu espiritu);
    Espiritu recuperar(Long idDelEspiritu);
    void actualizar(Espiritu espiritu);
    void eliminar(Long idDelEspiritu);
    List<Espiritu> recuperarTodos();
    void eliminarTodos();
    List<Espiritu> espiritusDemoniacos();
    List<Espiritu> espiritusDemoniacos(Direccion direccion, int pag, int cantPorPag);
}