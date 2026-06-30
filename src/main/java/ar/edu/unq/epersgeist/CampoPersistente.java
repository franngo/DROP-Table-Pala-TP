package ar.edu.unq.epersgeist;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

@Setter
@Getter
public class CampoPersistente {
    Field field;
    String nombreColumna;
    boolean esId;
    boolean esManyToOne;
    boolean esDiscriminador;
}
