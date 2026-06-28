package ar.edu.unq.epersgeist.persistencia.raven;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Cuervo {
    private String id;
    private String nombre;

    public Cuervo(String nombre) {
        this.nombre = nombre;
    }
}
