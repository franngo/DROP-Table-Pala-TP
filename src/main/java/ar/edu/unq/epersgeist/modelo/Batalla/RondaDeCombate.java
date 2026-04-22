package ar.edu.unq.epersgeist.modelo.Batalla;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;

public interface RondaDeCombate {
    void ataqueIndividual(Espiritu atacante, Espiritu atacado);

    boolean ataqueExitoso(Espiritu atacante, Espiritu atacado);
}
