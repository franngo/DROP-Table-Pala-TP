package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.persistencia.raven.cuervo.Cuervo;
import ar.edu.unq.epersgeist.persistencia.raven.cuervo.CuervoDaoRaven;
import ar.edu.unq.epersgeist.servicios.CuervoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuervoServiceImpl implements CuervoService {
    @Autowired
    private CuervoDaoRaven dao;

    @Override
    public Cuervo crear(String nombre) {
        return dao.guardar(new Cuervo(nombre));
    }

    @Override
    public Cuervo actualizar(Cuervo cuervo) {
        return dao.actualizar(cuervo);
    }

    @Override
    public Cuervo obtenerPorId(String id) {
        return dao.buscarPorId(id);
    }

    @Override
    public List<Cuervo> obtenerTodos() {
        return dao.recuperarTodos();
    }

    @Override
    public void eliminar(String id) {
        dao.eliminar(id);
    }

    @Override
    public void eliminarTodos() {
        dao.eliminarTodos();
    }
}