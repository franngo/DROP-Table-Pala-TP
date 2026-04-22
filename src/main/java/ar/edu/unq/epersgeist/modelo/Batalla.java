package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;

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
                itDemonios.remove();
            }
        }
    }
/*
    public void iniciarBatalla() {

        //angeles medium que ataca
        List<Angelical> angelesAtacante = atacante.espiritusAngelicales();
        List<Demonio> demoniosAtacados = atacado.espiritusDemonios();

        int indexDemonio = 0;
        for (Angelical angel : angelesAtacante) {

            //si la lista de demonios esta vacia, ganó!!
            if (demoniosAtacados.isEmpty()) {
                this.ganoAtacante = true;
                break;
            }
            //obtengo el demonio actual a atacar
            Demonio demonioAtacado = demoniosAtacados.get(indexDemonio);

            //Se calcula los porcentajes
            int porcentajeDeEXITO = this.porcentajeDeAtaqueExitoso(angel);
            int defensaDemonio = demonioAtacado.capacidadDefensiva();

            if (porcentajeDeEXITO > defensaDemonio) {
                demonioAtacado.recibirAtaque(angel.getNivelDeConexion() / 2);
                if (!demonioAtacado.estaActivo()) {
                    demoniosAtacados.remove(indexDemonio);
                    indexDemonio++;
                }
            } else {
                angel.recibirAtaque(5);
            }


        }

    }
*/


    public void accionesPostBatalla() {
        if (this.ganoAtacante) {
            atacado.desvincularDemonios();
        } else {
            atacante.desvincularAngeles();
        }
    }
}
