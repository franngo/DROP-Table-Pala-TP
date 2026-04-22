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
        batalla.accionesPostBatalla();// posible foreach entre listas
    }

//    private void iniciarExorcismo(Medium mediumAExorcizar) {
//        //mis angeles
//        List<Angelical> espiritusAngelicales = this.espiritusAngelicales();
//        // demonios enemigos
//        List<Demonio> demoniosMedium = new ArrayList<>(mediumAExorcizar.espiritusDemonios());
//
//
//        int indexDemonio = 0;
//
//        for (Angelical angelActual : espiritusAngelicales) {
//
//            if (demoniosMedium.isEmpty()) break;
//
//            Demonio demonioActualAtacado = demoniosMedium.get(indexDemonio);
//
//            //ATAQUE ENTRE ANGEL ACTUAL Y EL DEMONIO ACTUAL
//            this.ataqueADemonio(angelActual,demonioActualAtacado);
//
//           // seguir simulando pelea
//
//
//            // si el demonio murió lo saco de la lista de atacados
//            if (!demonioActualAtacado.estaActivo()) {
//                demoniosMedium.remove(demonioActualAtacado);
//            } else {
//                indexDemonio = (indexDemonio + 1) % demoniosMedium.size();
//            }
//        }
//
//
//
//    }


//    private void iniciarExorcismo(Medium mediumAExorcizar) {
//        List<Angelical> espiritusAngelicales = this.espiritusAngelicales();
//        List<Demonio> demoniosMediumAExorcizar = mediumAExorcizar.espiritusDemonios();
//
//        for (Angelical angel : espiritusAngelicales) {
//             int 0;
//            // Si ya no quedan demonios, termina el exorcismo
//            if (indiceDemonio >= demoniosMediumAExorcizar.size()) {
//                break;
//            }
//
//            Demonio demonio = demoniosMediumAExorcizar.get(indiceDemonio);
//
//            int porcentajeAtaque = Math.min(
//                    Random.numeroGenerado(1, 10) + angel.nivelDeConexion(),
//                    100
//            );
//
//            int porcentajeDefensa = Random.numeroGenerado(1, 100);
//
//            if (porcentajeAtaque > porcentajeDefensa) {
//
//                demonio.perderConexion(angel.nivelDeConexion() / 2);
//
//                if (demonio.nivelDeConexion() <= 0) {
//                    mediumAExorcizar.desvincular(demonio);
//                    demonios.remove(indiceDemonio);
//                }
//
//            } else {
//
//                angel.perderConexion(5);
//
//                if (angel.nivelDeConexion() <= 0) {
//                    this.desvincular(angel);
//                }
//            }
//        }
//    }


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

    public void desvincularAngeles() {
        this.espiritus.removeIf(esp -> esp instanceof Angelical);
    }

    public void desvincularDemonios() {
        this.espiritus.removeIf(esp -> esp instanceof Demonio);
    }
}