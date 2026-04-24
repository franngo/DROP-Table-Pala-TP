package ar.edu.unq.epersgeist.modelo.Espiritu;

public interface Combatiente {
    public void recibirAtaqueDe(Espiritu atacante);

    boolean estaActivo();
}
