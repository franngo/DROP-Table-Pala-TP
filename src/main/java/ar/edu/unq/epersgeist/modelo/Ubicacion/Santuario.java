package ar.edu.unq.epersgeist.modelo.Ubicacion;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

// --------------------------------------------------------------------------------

@NoArgsConstructor

@Entity
@DiscriminatorValue("SANTUARIO")

// --------------------------------------------------------------------------------

public class Santuario extends Ubicacion {

    public Santuario(String nombre, Integer energia) {
        super(nombre, energia);
    }

    @Override
    public void aplicarEfectoMovimiento(Espiritu espiritu) {
        espiritu.aplicarEfectoDeSantuario();
    }
}
