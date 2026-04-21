package ar.edu.unq.epersgeist.modelo.random;

import lombok.Setter;

@Setter
public class Random {
    
    private static EstrategiaNumero estrategiaNumero = new EstrategiaAleatoria10();

    public static int numeroGenerado() {
        return estrategiaNumero.numeroGenerado();
    }
}
