package ar.edu.unq.epersgeist.modelo.Batalla;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.List;

public class Batalla {

    Medium atacante;
    Medium atacado;

    private RondaDeCombateStandard ronda = new RondaDeCombateStandard();

    public Batalla(Medium medium, Medium mediumAExorcizar) {

        this.atacante = medium;
        this.atacado = mediumAExorcizar;
    }

    public void iniciarBatalla() {
        List<Angelical> angeles = atacante.espiritusAngelicales();
        List<Demonio> demonios = atacado.espiritusDemonios();

        //Iterator<Demonio> itDemonios = demonios.iterator();
        int indexDemonios = 0;
        // por cada angel hay un ataque a la lista de demonios
        for (Angelical angel : angeles) {

            // si no hay mas demonios, se considera terminado el exorcismo
           /* if (!itDemonios.hasNext()) {
                break;
            }*/
            if (demonios.isEmpty()) {
                break;
            }
            // obtengo el demonio actual
            Demonio demonio = demonios.get(indexDemonios);

            //batalla individual entre angel y demonio
            ronda.ataqueIndividual(angel, demonio);


            if (!demonio.estaActivo()) {
                demonio.desvincularse();
                demonios.remove(indexDemonios);
                indexDemonios++;
            }

            if (!angel.estaActivo()) {
                angel.desvincularse();
            }
        }
    }


}
