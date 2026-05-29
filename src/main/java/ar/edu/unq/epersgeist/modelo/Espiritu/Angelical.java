package ar.edu.unq.epersgeist.modelo.Espiritu;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.request.TipoEspiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Cementerio;
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
@DiscriminatorValue("ANGEL")
public class Angelical extends Espiritu implements Combatiente {
    public Angelical(@NonNull String nombre, Ubicacion ubicacion) {
        super(nombre, ubicacion);
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

    @Override
    public void cambiosConexion(Ubicacion ubicacion) {
        if (ubicacion instanceof Cementerio) {
            this.afectarConexion(Cementerio.DANIO_A_ESPIRITU);
        }
    }

    public int porcentajeDescansarEn(Ubicacion ubicacion) {
        return ubicacion.porcentajeDescansarAngelical();
    }

    @Override
    public boolean puedeInvocarseEn(Ubicacion ubicacion) {
        return ubicacion.puedeInvocarseUnAngelical();
    }

    @Override
    public TipoEspiritu getTipoEspiritu() { return TipoEspiritu.ANGELICAL; }

}
