package ar.edu.unq.epersgeist.modelo.Espiritu;

public interface Combatiente {
    void recibirAtaqueDe(Espiritu atacante);

    boolean estaActivo();
}
