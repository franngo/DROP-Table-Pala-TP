package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.servicios.MediumService;

import java.util.List;

//TODO IMPLEMENTAR
/*
 *
 * Servicio Medium
 * */
public class MediumServiceImpl implements MediumService {
    @Override
    public Medium crear(Medium medium) {
        return null;
    }

    @Override
    public Medium recuperar(Long idMedium) {
        return null;
    }

    @Override
    public void actualizar(Medium medium) {

    }

    @Override
    public void eliminar(Long idMedium) {

    }

    @Override
    public List<Medium> recuperarTodos() {
        return List.of();
    }

    @Override
    public void descanzar(Long id) {

    }

    @Override
    public void exorcizar(Long idatacante, Long idAtacado) {

    }

    @Override
    public List<Medium> espiritus() {
        return List.of();
    }

    @Override
    public Espiritu invocar(Long id, Long idMedium) {
        return null;
    }
}
