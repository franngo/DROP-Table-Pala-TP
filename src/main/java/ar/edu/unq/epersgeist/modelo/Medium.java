package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.exceptions.ExorcistaSinAngelesException;
import ar.edu.unq.epersgeist.modelo.random.Random;
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
        this.iniciarExorcismo(mediumAExorcizar); // posible foreach entre listas
    }

    private void iniciarExorcismo(Medium mediumAExorcizar) {
    }

    private void validarExorcismo() throws ExorcistaSinAngelesException {
        if (!this.tengoAlgunEspirituAngel()){
            throw new ExorcistaSinAngelesException("El medium no cuenta con espiritus angeles");
        }
    }

    private List<Angelical> espiritusAngelicales() {
        return espiritus.stream()
                .filter(e -> e instanceof Angelical)
                .map(e -> (Angelical) e)
                .toList();
    }

    private List<Demonio> espiritusDemonios() {
        return espiritus.stream()
                .filter(e -> e instanceof Demonio)
                .map(e -> (Demonio) e)
                .toList();
    }



    private boolean tengoAlgunEspirituAngel(){
        return espiritus.stream().anyMatch( e -> e instanceof Angelical);
    }

    public int porcentajeDeAtaqueExitoso(Espiritu espiritu){
        return Random.numeroGenerado() + espiritu.getNivelDeConexion();
    }



}