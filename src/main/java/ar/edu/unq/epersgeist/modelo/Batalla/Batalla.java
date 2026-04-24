package ar.edu.unq.epersgeist.modelo.Batalla;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Batalla {

    Medium atacante;
    Medium atacado;

    private RondaDeCombate ronda = new RondaDeCombateStandard();

    public Batalla(Medium medium, Medium mediumAExorcizar) {

        this.atacante = medium;
        this.atacado = mediumAExorcizar;
    }

    public void iniciarBatalla() {
        List<Angelical> angeles = atacante.espiritusAngelicales();
        List<Demonio> demonios = new ArrayList<>(atacado.espiritusDemonios());

        Iterator<Demonio> itDemonios = demonios.iterator();

        if (!itDemonios.hasNext()) return;

        Demonio demonioActual = itDemonios.next();

        for (Angelical angel : angeles) {

            ronda.ataqueIndividual(angel, demonioActual);

            if (!demonioActual.estaActivo()) {
                demonioActual.desvincularse();

                itDemonios.remove();

                if (!itDemonios.hasNext()) {
                    break;
                }

                demonioActual = itDemonios.next();
            }

            if (!angel.estaActivo()) {
                angel.desvincularse();
            }
        }
    }

}
