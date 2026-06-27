package ar.edu.unq.epersgeist.persistencia.raven.cuervo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Cuervo {
    private String id;
    private String nombre;

    public Cuervo() {
    }

    public Cuervo(String nombre, Integer edad) {
        this.nombre = nombre;
        
    }

}
