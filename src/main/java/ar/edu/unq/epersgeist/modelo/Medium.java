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
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDeInvocacionDeEspirituException;

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
        this.recuperarMana(Medium.manaDescansoMedium);
        this.espiritusRecuperanMana(Medium.manaDescansoEspiritus);
    }

    public static final int manaDescansoMedium = 15;
    public static final int manaDescansoEspiritus = 5;

    private void recuperarMana(int cantidad) {
        this.mana = Math.min(this.manaMax, this.cantidadAlDescansar(cantidad));
    }

    private int cantidadAlDescansar(int cantidad){
        return this.mana + cantidad;
    }

    private void espiritusRecuperanMana(int cantidad) {
        for (Espiritu espiritu : espiritus) {
            espiritu.fortalecerConexion(cantidad);
        }
    }

    /*
    Precondición = El Espiritu dado debe estar libre (no debe estar conectado con un Medium).
    Observación = Si el medium no tiene al menos 10 de maná, no se lleva la operación a cabo (no sucede nada).
    */
    public Espiritu invocar(Espiritu espiritu) {
        if(this.mana < 10) {
            return espiritu; //si no hay suificiente maná, no se hace nada
        }

        if(espiritu.getMediumCon() != null) { //si el Espiritu NO está libre
            throw new ErrorDeInvocacionDeEspirituException();
        }

        this.mana = Math.max(minimoMana, this.cantidadAlInvocar(manaRequeridoInvocacion));

        espiritu.setUbicacion(this.ubicacion);
        this.ubicacion.agregarEspiritu(espiritu);

        return espiritu;
    }

    public static final int minimoMana = 0;
    public static final int manaRequeridoInvocacion = 10;

    private int cantidadAlInvocar(int cantidad){
        return this.mana - cantidad;
    }

}