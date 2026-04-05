package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.jdbc.JDBCEspirituDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;

import java.util.List;

public class EspirituServiceImpl implements EspirituService {
    private JDBCEspirituDAO espirituDao;

    public EspirituServiceImpl(JDBCEspirituDAO espirituDao) {
        this.espirituDao = espirituDao;
    }

    @Override
    public Espiritu crear(Espiritu espiritu) {
        return espirituDao.crear(espiritu);
    }

    @Override
    public Espiritu recuperar(Long espirituId) {

        return espirituDao.recuperar(espirituId);

    }

    @Override
    public void actualizar(Espiritu espiritu) {
        espirituDao.actualizar(espiritu);
    }

    @Override
    public void eliminar(Long espirituId) {
        espirituDao.eliminar(espirituId);

    }

    @Override
    public List<Espiritu> recuperarTodos() {
        return espirituDao.recuperarTodos();
    }

    //    @Override
//    public Medium conectar(Long espirituId, Medium medium) {
//        //lo pido de la base
//        Espiritu espirituAConectar = espirituDao.recuperar(espirituId);
//        // aplico la logica de negocio ENTRE MODELOS
//        espirituAConectar.aumentarConexion(medium);
//        //persisto los cambios
//        espirituDao.actualizar(espirituAConectar);
//        return medium;
//    }
    @Override
    public Medium conectar(Long espirituId, Medium medium) {

        System.out.println("👉 Entrando a conectar()");
        System.out.println("📌 espirituId: " + espirituId);
        System.out.println("📌 medium: " + medium);

        // lo pido de la base
        System.out.println("🔍 Recuperando espiritu de la base...");
        Espiritu espirituAConectar = espirituDao.recuperar(espirituId);
        System.out.println("✅ Espiritu recuperado: " + espirituAConectar);
        System.out.println("🔗 Nivel de conexión actual: " + espirituAConectar.getNivelDeConexion());

        // aplico la lógica de negocio
        System.out.println("⚙️ Aplicando lógica de conexión...");
        System.out.println("ESPIRITUCONECTAR");
        System.out.println(espirituAConectar.hashCode());
        espirituAConectar.aumentarConexion(medium);

        System.out.println("🔗 Nuevo nivel de conexión: " + espirituAConectar.getNivelDeConexion());

        // persisto los cambios
        System.out.println("💾 Persistiendo cambios...");
        espirituDao.actualizar(espirituAConectar);
        System.out.println("✅ Cambios persistidos");

        System.out.println("👋 Saliendo de conectar()");

        return medium;
    }
}
