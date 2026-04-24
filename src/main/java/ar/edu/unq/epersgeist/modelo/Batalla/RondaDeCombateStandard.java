package ar.edu.unq.epersgeist.modelo.Batalla;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.random.Random;
import ar.edu.unq.epersgeist.modelo.random.RandomDefensa;

public class RondaDeCombateStandard implements RondaDeCombate {

    public void ataqueIndividual(Espiritu angel, Espiritu demonio) {

        if (ataqueExitoso(angel, demonio)) {
            demonio.recibirAtaqueDe(angel);
        } else {
            angel.recibirAtaqueDe(demonio);
        }
    }

    public boolean ataqueExitoso(Espiritu angel, Espiritu demonio) {
        return this.porcentajeDeAtaqueExitoso(angel) > this.capacidadDefensivaDemonio();
    }


    public int porcentajeDeAtaqueExitoso(Espiritu espiritu) {
        return (int) Math.min(Random.numeroGenerado() + espiritu.getNivelDeConexion(), 100);
    }

    public int capacidadDefensivaDemonio() {
        return (int) RandomDefensa.numeroGenerado();
    }


}
