package ar.edu.unq.epersgeist.modelo.Batalla;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.Iterator;
import java.util.List;

public class Batalla {
    boolean ganoAtacante;
    Medium atacante;
    Medium atacado;

    private RondaDeCombate ronda = new RondaDeCombate();

    public Batalla(Medium medium, Medium mediumAExorcizar) {
        this.ganoAtacante = false;
        this.atacante = medium;
        this.atacado = mediumAExorcizar;
    }

    public void iniciarBatalla() {
        List<Angelical> angeles = atacante.espiritusAngelicales();
        List<Demonio> demonios = atacado.espiritusDemonios();

        Iterator<Demonio> itDemonios = demonios.iterator();

        // por cada angel hay un ataque a la lista de demonios
        for (Angelical angel : angeles) {

            // si no hay mas demonios, se considera que ganó
            if (!itDemonios.hasNext()) {
                ganoAtacante = true;
                break;
            }
            // obtengo el demonio actual
            Demonio demonio = itDemonios.next();

            //batalla individual entre angel y demonio
            ronda.ataqueIndividual(angel, demonio);


            if (!demonio.estaActivo()) {
                demonio.desvincularse();
                itDemonios.remove();
            }

            if (!angel.estaActivo()) {
                angel.desvincularse();
            }
        }
    }

    public void accionesPostBatalla() {
        if (this.ganoAtacante) {
            atacado.desvincularDemonios();
        } else {
            atacante.desvincularAngeles();
        }
    }
}
