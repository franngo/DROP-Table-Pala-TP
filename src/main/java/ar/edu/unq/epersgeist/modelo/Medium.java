package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Medium implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nombre;
    private Integer manaMax;
    private Integer mana;
    private List<Espiritu> espiritus;
    private int cantFortalece;

    public Medium(@NonNull String nombre, @NonNull Integer manaMax, @NonNull Integer mana) {
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