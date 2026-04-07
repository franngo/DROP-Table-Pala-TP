package ar.edu.unq.epersgeist.modelo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Medium implements Serializable {

    private String nombre;
    private Integer manaMax;
    private Integer mana;
    private List<Espiritu> espiritus;
    private int cantFortalece;

    public Medium(String nombre, Integer manaMax, Integer mana) {
        this.espiritus = new ArrayList<>();
        this.nombre = nombre;
        this.manaMax = manaMax;
        this.mana = mana;
        this.cantFortalece = 10;
    }

    public void agregarEspiritu(Espiritu espiritu) {
        espiritus.add(espiritu);
    }
    
}