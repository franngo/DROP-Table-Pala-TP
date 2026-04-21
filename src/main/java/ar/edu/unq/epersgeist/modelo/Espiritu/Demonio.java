package ar.edu.unq.epersgeist.modelo.Espiritu;

import jakarta.persistence.Entity;

@Entity
public class Demonio extends Espiritu {


    public boolean estaActivo() {
        return this.getNivelDeConexion() > 0;
    }

    public void perderConexion() {

    }
}
