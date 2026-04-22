package ar.edu.unq.epersgeist.modelo.Espiritu;

import jakarta.persistence.Entity;

@Entity
public class Demonio extends Espiritu implements Combatiente {


    public boolean estaActivo() {
        return this.getNivelDeConexion() > 0;
    }

    public void recibirAtaqueDe(Espiritu angelAtacante) {
        int danhoARecibir = angelAtacante.getNivelDeConexion() / 2;
        // le baja el nivel de conexion segun el calculo en el ataque y la deja en un valor o en 0
        this.setNivelDeConexion(Math.max(0, this.getNivelDeConexion() - danhoARecibir));
    }
}
