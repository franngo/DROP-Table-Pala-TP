package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Medium;

public interface MediumDAO {
    Medium crear(Medium medium);

    Medium recuperar(Long idDelMedium);

    void actualizar(Medium medium);

    void eliminar(Long idDelMedium);
}
