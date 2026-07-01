package ar.edu.unq.epersgeist;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

@Setter
@Getter
public class CampoPersistente {
    private Field field; //Un campo que existe realmente en la entidad. Es null si es un campo virtual
    private String nombreColumna;

    private boolean esId;
    private boolean esManyToOne;
    private boolean esDiscriminador;

    private Object valorConstante;    // Solo se usa para campos virtuales (x ej: un valor discriminador como "SANTUARIO" o "CEMENTERIO")
}
