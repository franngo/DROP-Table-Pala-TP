package ar.edu.unq.epersgeist.modelo.Espiritu;

import jakarta.persistence.Entity;
import lombok.NonNull;

@Entity
public class Demonio extends Espiritu implements Combatiente {


    public Demonio(@NonNull String tipo, @NonNull String nombre) {
        super(tipo, nombre);
    }

    public boolean estaActivo() {
        return this.getNivelDeConexion() > 0;
    }

    @Override
    public void recibirAtaqueDe(Espiritu angelAtacante) {
        int danhoARecibir = angelAtacante.getNivelDeConexion() / 2;
        // le baja el nivel de conexion segun el calculo en el ataque y la deja en un valor o en 0
        this.setNivelDeConexion(Math.max(0, this.getNivelDeConexion() - danhoARecibir));
    }
}
