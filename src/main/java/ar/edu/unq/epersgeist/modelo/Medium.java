package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Batalla.Batalla;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.exceptions.ExorcistaSinAngelesException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

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
        espiritu.setMediumCon(this);
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

    public void exorcizar(Medium mediumAExorcizar) throws ExorcistaSinAngelesException {
        this.validarExorcismo();

        Batalla batalla = new Batalla(this, mediumAExorcizar);
        batalla.iniciarBatalla();

    }

    private void validarExorcismo() throws ExorcistaSinAngelesException {
        if (!this.tengoAlgunEspirituAngel()) {
            throw new ExorcistaSinAngelesException("El medium no cuenta con espiritus angeles");
        }
    }

    public List<Angelical> espiritusAngelicales() {
        return espiritus.stream()
                .filter(e -> e instanceof Angelical)
                .map(e -> (Angelical) e)
                .toList();
    }

    public List<Demonio> espiritusDemonios() {
        return espiritus.stream()
                .filter(e -> e instanceof Demonio)
                .map(e -> (Demonio) e)
                .toList();
    }


    private boolean tengoAlgunEspirituAngel() {
        return espiritus.stream().anyMatch(e -> e instanceof Angelical);
    }

    /*
     * el metodo desvncularseDe lo llama el espiritu cuando se libera
     * */
    public void desvincularseDe(Espiritu espiritu) {
        this.espiritus.remove(espiritu);
    }


}