package ar.edu.unq.epersgeist.modelo.Ubicacion;

import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.response.TipoUbicacionResponse;
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

    public static final double DANIO_A_ESPIRITU = 5;

    @Override
    public int porcentajeDescansarMedium() {
        return 50;
    }

    @Override
    public int porcentajeDescansarAngelical() {
        return 0;
    }

    @Override
    public int porcentajeDescansarDemonio() {
        return 100;
    }

    @Override
    public boolean puedeInvocarseUnAngelical() {
        return false;
    }

    @Override
    public boolean puedeInvocarseUnDemonio() {
        return true;
    }

    @Override
    public TipoUbicacionResponse getTipoUbicacion() {
        return TipoUbicacionResponse.CEMENTERIO;
    }

}
