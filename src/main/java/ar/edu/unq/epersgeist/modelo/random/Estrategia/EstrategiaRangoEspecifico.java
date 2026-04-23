package ar.edu.unq.epersgeist.modelo.random.Estrategia;

import lombok.Getter;

import java.util.random.RandomGenerator;

@Getter
public class EstrategiaRangoEspecifico implements EstrategiaNumero {
    private final int min;
    private final int max;

    public EstrategiaRangoEspecifico(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public int numeroGenerado() {
        if (min == max) {
            return min;
        }

        return RandomGenerator.getDefault().nextInt(min, max + 1);
    }
}
