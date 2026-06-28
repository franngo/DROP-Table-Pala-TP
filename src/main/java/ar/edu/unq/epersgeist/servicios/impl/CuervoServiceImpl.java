package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.persistencia.raven.Cuervo;
import ar.edu.unq.epersgeist.persistencia.raven.impl.CuervoDaoRavenImpl;
import ar.edu.unq.epersgeist.servicios.CuervoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuervoServiceImpl implements CuervoService {
    @Autowired
    private CuervoDaoRavenImpl cuervoDao;

    // --------------------------------------------------------------------------------

    @Override
    public Cuervo crear(String nombre) {
        Cuervo nuevoCuervo = new Cuervo(nombre);
        return cuervoDao.save(nuevoCuervo);
    }
    // --------------------------------------------------------------------------------

    @Override
    public Cuervo recuperar(Long id) {
        return cuervoDao.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el cuervo con ID: " + id));
    }

    @Override
    public List<Cuervo> recuperarTodos() {
        return cuervoDao.findAll();
    }

    // --------------------------------------------------------------------------------

    @Override
    public void actualizar(Cuervo cuervo) {
        cuervoDao.save(cuervo);
    }

    // --------------------------------------------------------------------------------

    @Override
    public void eliminar(Long id) {
        boolean eliminado = cuervoDao.deleteById(id);
        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar: no existe el cuervo con ID: " + id);
        }
    }

    @Override
    public void eliminarTodos() {
        cuervoDao.deleteAll();
    }

    // --------------------------------------------------------------------------------

    @Override
    public List<Cuervo> recuperarHistorial(Long id) {
        return cuervoDao.findRevisionsById(id);
    }
}
