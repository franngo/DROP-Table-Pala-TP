package ar.edu.unq.epersgeist.modelo.Ubicacion;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

// --------------------------------------------------------------------------------

@NoArgsConstructor

@Entity
@DiscriminatorValue("CEMENTERIO")

// --------------------------------------------------------------------------------

public class Cementerio extends Ubicacion {
    public Cementerio(String nombre) {
        super(nombre);
    }

    @Override
    public void aplicarEfectoMovimiento(Espiritu espiritu) {
        espiritu.aplicarEfectoDeCementerio();
    }
}
