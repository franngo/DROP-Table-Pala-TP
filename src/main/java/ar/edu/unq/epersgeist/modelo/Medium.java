package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Batalla.Batalla;
import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDeInvocacionDeEspirituException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorManaMayorQueManaMaxAlCrearMedium;
import ar.edu.unq.epersgeist.modelo.exceptions.ExorcistaSinAngelesException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Medium implements Serializable {
    public static final int minimoMana = 0;
    public static final int manaRequeridoInvocacion = 10;
    public static final double porcentajeAFortalecer = 0.20; //para calcular el 20% del maná del medium, que es lo que recupera un espiritu al conectar
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nombre;
    private Integer manaMax;
    private Integer mana;
    @OneToMany(mappedBy = "mediumCon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Espiritu> espiritus;
    @ManyToOne
    private Ubicacion ubicacion;

    public Medium(@NonNull String nombre, @NonNull Integer manaMax, @NonNull Integer mana, Ubicacion ubicacion) {
        this.validarManaMenorQueManaMax(mana, manaMax);

        this.espiritus = new ArrayList<>();
        this.nombre = nombre;
        this.manaMax = manaMax;
        this.mana = mana;
        this.ubicacion = ubicacion;
    }

    public void validarManaMenorQueManaMax(Integer mana, Integer manaMax) {
        if (mana > manaMax) {
            throw new ErrorManaMayorQueManaMaxAlCrearMedium();
        }
    }

    public void agregarEspiritu(Espiritu espiritu) {
        espiritus.add(espiritu);
        espiritu.vincular(this);
    }

    public void descansar() {
        this.recuperarMana();
        this.espiritusRecuperanMana();
    }

    private void recuperarMana() {
        this.mana = Math.min(this.manaMax, this.cantidadAlDescansar());
    }

    private int cantidadAlDescansar() {
        int recarga = this.ubicacion.porcentajeDescansarMedium() * this.mana / 100;
        return this.mana + recarga;
    }

    private void espiritusRecuperanMana() {
        for (Espiritu espiritu : espiritus) {
            espiritu.fortalecerConexionDescansandoEn(this.ubicacion);
        }
    }

    public Double cantidadDeFortalecimientoDeConexion() {
        return this.mana * porcentajeAFortalecer;
    }

    /*
    Precondición = El Espiritu dado debe estar libre (no debe estar conectado con un Medium).
    Observación = Si el medium no tiene al menos 10 de maná, no se lleva la operación a cabo (no sucede nada).
    */
    public Espiritu invocar(Espiritu espiritu) {
        if (this.mana < 10) {
            return espiritu; //si no hay suificiente maná, no se hace nada
        }

        if (!espiritu.estaLibre()) { //El Espiritu dado debe estar libre
            throw new ErrorDeInvocacionDeEspirituException("El espiritu dado no está libre, por lo que no puede ser invocado por el medium dado.");
        }

        if (!espiritu.puedeInvocarseEn(this.ubicacion)) {
            throw new ErrorDeInvocacionDeEspirituException("Un espíritu del tipo dado no se puede invocar en la ubicación actual ni ninguna otra de su mismo tipo");
        }

        this.mana = Math.max(minimoMana, this.cantidadAlInvocar(manaRequeridoInvocacion));

        //esto cambiara la logica teniendo en cuenta que al invocar dependiendo el lugar cambian la conexion
        espiritu.moverseA(this.ubicacion);

        return espiritu;
    }

    private int cantidadAlInvocar(int cantidad) {
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
     * El metodo desvincularseDe lo llama el Espiritu cuando se libera
     * */
    public void desvincularseDe(Espiritu espiritu) {
        this.espiritus.remove(espiritu);
    }


    public void moverA(Ubicacion nuevaUbicacion) {

        this.setUbicacion(nuevaUbicacion);
        this.moverEspiritusA(getUbicacion());
    }

    public void moverEspiritusA(Ubicacion nuevaUbicacion) {

        List<Espiritu> aDesvincular = new ArrayList<>();
        this.espiritus.forEach(e -> {
            e.moverseA(nuevaUbicacion);

            if (e.sinConexion()) {
                aDesvincular.add(e);
            }
        });
        aDesvincular.forEach(Espiritu::desvincularse);
    }
}