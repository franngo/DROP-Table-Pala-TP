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
        this.actualizarConexion(10);
        //this.setNivelDeConexion(10);
        medium.agregarEspiritu(this);

    }

    private void actualizarConexion(int cantidad) {
        //  if (this.nivelDeConexion + cantidad < 100) {
        //       this.nivelDeConexion = this.getNivelDeConexion() + cantidad;
        //  }
        this.nivelDeConexion = this.nivelDeConexion + cantidad;
    }


}
