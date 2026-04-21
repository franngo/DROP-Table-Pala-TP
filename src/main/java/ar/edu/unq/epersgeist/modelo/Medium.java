package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Medium implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nombre;
    private Integer manaMax;
    private Integer mana;
    private int cantFortalece;

    @OneToMany(mappedBy = "mediumCon", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Espiritu> espiritus;

    @ManyToOne
    private Ubicacion ubicacion;

    public Medium(@NonNull String nombre, @NonNull Integer manaMax, @NonNull Integer mana) {
        this.espiritus = new ArrayList<Espiritu>();
        this.nombre = nombre;
        this.manaMax = manaMax;
        this.mana = mana;
        this.cantFortalece = 10;
    }

    public void agregarEspiritu(Espiritu espiritu) {
        espiritus.add(espiritu);
        espiritu.setMediumCon(this);
    }

    public void descansar() {

    }

    /*
    El de MediumService es Espiritu invocar(Long id, Long idMedium);
    Recibe ambos id, recupera los 2, y se debería fijar que ambos no sean cosas nulas.
    Si son nulas ¿ErrorAlInvocar("Se intentó invocar con una entidad no existente.")?
    Si no lo son, y tenemos medium y espiritu, mandamos medium.invocar(espiritu);

    Espiritu invocar(Espiritu espiritu) {
        if(this.mana < 10) {
            return;
        }

        if(!espiritu.estaLibre()) {
            throw ErrorAlInvocar("El espiritu dado no está libre, por lo que no puede ser invocado por otro medium.");
        }

        this.mana -= 10; //en realidad hay que fijarse que quede en eso o en un mínimo de 0, pero NO en negativo
        //usar Math.min

        espiritu.setUbicacion(this.ubicacion);
    }
     */

}