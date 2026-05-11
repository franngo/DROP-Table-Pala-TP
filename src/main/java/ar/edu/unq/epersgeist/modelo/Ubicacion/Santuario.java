package ar.edu.unq.epersgeist.modelo.Ubicacion;

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
    public double dañoAEspiritu() {
        return 10;
    }
}
