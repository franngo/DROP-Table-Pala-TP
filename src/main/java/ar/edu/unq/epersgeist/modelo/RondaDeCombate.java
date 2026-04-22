package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.random.Random;
import ar.edu.unq.epersgeist.modelo.random.RandomDefensa;

public class RondaDeCombate {

    public void ataqueIndividual(Angelical angel, Demonio demonio) {

        if (ataqueExitoso(angel, demonio)) {
            demonio.recibirAtaque(angel.poderDeAtaque());
        } else {
            angel.recibirAtaque(5);
        }
    }

    private boolean ataqueExitoso(Angelical angel, Demonio demonio) {
        return this.porcentajeDeAtaqueExitoso(angel) > this.capacidadDefensiva(demonio);
    }


    public int porcentajeDeAtaqueExitoso(Espiritu espiritu) {
        return (int) Math.max(Random.numeroGenerado() + espiritu.getNivelDeConexion(), 100);
    }

    public int capacidadDefensiva() {
        return (int) RandomDefensa.numeroGenerado();
    }

}
