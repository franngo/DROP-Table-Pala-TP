package ar.edu.unq.epersgeist.modelo.random.Estrategia;

import java.util.random.RandomGenerator;

public class EstrategiaAleatoria10 implements EstrategiaNumero {


    @Override
    public int numeroGenerado() {
        return RandomGenerator.getDefault().nextInt(1, 11);
    }
}

