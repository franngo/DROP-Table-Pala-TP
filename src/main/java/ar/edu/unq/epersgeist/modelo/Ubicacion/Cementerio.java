package ar.edu.unq.epersgeist.modelo.Ubicacion;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

// --------------------------------------------------------------------------------

@NoArgsConstructor

@Entity
@DiscriminatorValue("CEMENTERIO")

// --------------------------------------------------------------------------------

public class Cementerio extends Ubicacion {
    public Cementerio(String nombre, Integer energia) {
        super(nombre, energia);
    }

//    @Override
//    public void aplicarEfectoMovimiento(Espiritu espiritu) {
//        espiritu.aplicarEfectoDeCementerio();
//    }

    @Override
    public double dañoAEspiritu() {
        return 5;
    }
}
