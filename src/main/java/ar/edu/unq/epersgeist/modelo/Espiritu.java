package ar.edu.unq.epersgeist.modelo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Espiritu implements Serializable {

    private Long id;
    private String tipo;
    private Integer nivelDeConexion;
    private String nombre;

    public Espiritu(String tipo, String nombre) {

        this.tipo = tipo;
        this.nivelDeConexion = 0;
        this.nombre = nombre;
    }

    public void aumentarConexion(Medium medium) {
        this.fortalecerConexion(medium.getCantFortalece());
        medium.agregarEspiritu(this);

    }

    // TODO discutir si este metodo deberia ser privado ya que no es un caso de uso
    // pero tuve que hacerlo privado para testear
    public void fortalecerConexion(int cantidad) {
        if (this.nivelDeConexion + cantidad < 100) {
            this.nivelDeConexion = this.getNivelDeConexion() + cantidad;
        }

    }
}
