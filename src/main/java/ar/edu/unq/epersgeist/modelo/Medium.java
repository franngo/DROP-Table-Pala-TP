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

    //¿sería @OneToMany acá y @ManyToOne del lado del Espiritu?
    @OneToMany(mappedBy = "mediumCon", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Espiritu> espiritus;

    public Medium(@NonNull String nombre, @NonNull Integer manaMax, @NonNull Integer mana) {
        this.espiritus = new ArrayList<Espiritu>();
        this.nombre = nombre;
        this.manaMax = manaMax;
        this.mana = mana;
        this.cantFortalece = 10;
    }

    public void agregarEspiritu(Espiritu espiritu) {
        espiritus.add(espiritu);
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