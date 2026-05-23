package ar.edu.unq.epersgeist.modelo.Ubicacion;

import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.TipoUbicacion;
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

    public static final double DANIO_A_ESPIRITU = 10;

    @Override
    public int porcentajeDescansarMedium() {
        return 150;
    }

    @Override
    public int porcentajeDescansarAngelical() {
        return 100;
    }

    @Override
    public int porcentajeDescansarDemonio() {
        return 0;
    }

    @Override
    public boolean puedeInvocarseUnAngelical() {
        return true;
    }

    @Override
    public boolean puedeInvocarseUnDemonio() {
        return false;
    }

    @Override
    public TipoUbicacion getTipoUbicacion() {
        return TipoUbicacion.SANTUARIO;
    }


}
