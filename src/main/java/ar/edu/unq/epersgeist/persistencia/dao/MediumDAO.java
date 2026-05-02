package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Medium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediumDAO extends JpaRepository<Medium, Long> {
//    Medium crear(Medium medium);
//
//    Medium recuperar(Long idDelMedium);
//
//    void actualizar(Medium medium);
//
//    void eliminar(Long idDelMedium);
//
//    public List<Medium> recuperarTodos();
//
//    public void eliminarTodos();


}
