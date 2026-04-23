package ar.edu.unq.epersgeist.modelo.Espiritu;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NonNull;

@Entity
@DiscriminatorValue("ANGEL")
public class Angelical extends Espiritu implements Combatiente {
    public Angelical(@NonNull String nombre) {
        super(nombre);
    }

    @Override
    public boolean estaActivo() {
        return this.getNivelDeConexion() > 0;
    }

    @Override
    public void recibirAtaqueDe(Espiritu demonioAtacante) {
        // le baja el nivel de conexion segun el calculo en el ataque y la deja en un valor o en 0
        this.setNivelDeConexion(Math.max(0, this.getNivelDeConexion() - 5));
    }
}
