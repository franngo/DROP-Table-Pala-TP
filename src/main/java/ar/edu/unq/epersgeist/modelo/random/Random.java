package ar.edu.unq.epersgeist.modelo.random;

import ar.edu.unq.epersgeist.modelo.random.Estrategia.EstrategiaAleatoria10;
import ar.edu.unq.epersgeist.modelo.random.Estrategia.EstrategiaNumero;
import lombok.Setter;

@Setter
public class Random {

    @Setter
    private static EstrategiaNumero estrategiaNumero = new EstrategiaAleatoria10();

    public static int numeroGenerado() {
        return estrategiaNumero.numeroGenerado();
    }

}
