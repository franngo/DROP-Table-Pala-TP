package ar.edu.unq.epersgeist.modelo.Espiritu;

import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor


@Entity
@DiscriminatorValue("DEMONIO")
public class Demonio extends Espiritu implements Combatiente {


    public Demonio(@NonNull String nombre, Ubicacion ubicacion) {
        super(nombre, ubicacion);
    }

    public boolean estaActivo() {
        return this.getNivelDeConexion() > 0;
    }

    @Override
    public void recibirAtaqueDe(Espiritu angelAtacante) {


        double danhoARecibir = angelAtacante.getNivelDeConexion() / 2;

        // le baja el nivel de conexion segun el calculo en el ataque y la deja en un valor o en 0
        this.setNivelDeConexion(Math.max(0, this.getNivelDeConexion() - danhoARecibir));
    }
}
