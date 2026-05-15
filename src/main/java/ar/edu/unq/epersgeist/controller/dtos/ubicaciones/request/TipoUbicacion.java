package ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request;

import ar.edu.unq.epersgeist.modelo.Ubicacion.Cementerio;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Santuario;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;

public enum TipoUbicacion {
    CEMENTERIO {
        @Override
        public Cementerio crear(String nombre,
                                Integer energia) {

            return new Cementerio(nombre, energia);
        }
    },

    SANTUARIO {
        @Override
        public Santuario crear(String nombre,
                               Integer energia) {

            return new Santuario(nombre, energia);
        }
    };

    public abstract Ubicacion crear(String nombre,
                                    Integer energia);
}
