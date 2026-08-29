package ar.edu.unq.epersgeist.controller.dtos.espiritus.request;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;

public enum TipoEspiritu {

    ANGELICAL {
        @Override
        public Espiritu crear(String nombre,
                              Ubicacion ubicacion) {

            return new Angelical(nombre, ubicacion);
        }
    },

    DEMONIO {
        @Override
        public Espiritu crear(String nombre,
                              Ubicacion ubicacion) {

            return new Demonio(nombre, ubicacion);
        }
    };

    public abstract Espiritu crear(String nombre,
                                   Ubicacion ubicacion);
}
