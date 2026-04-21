package ar.edu.unq.epersgeist.modelo.random;

import lombok.Setter;

@Setter
public class RandomDefensa {
    private static EstrategiaNumero estrategiaNumero = new EstrategiaAleatoria100();

    public static int numeroGenerado() {
        return estrategiaNumero.numeroGenerado();
    }
}
