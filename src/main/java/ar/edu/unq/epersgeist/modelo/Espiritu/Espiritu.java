package ar.edu.unq.epersgeist.modelo.Espiritu;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.random.RandomDefensa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter @NoArgsConstructor

@Entity
public class Espiritu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String tipo;
    private Integer nivelDeConexion;
    private String nombre;

    @ManyToOne
    private Medium mediumCon;

    public Espiritu(@NonNull String tipo, @NonNull String nombre) {
        this.tipo = tipo;
        this.nivelDeConexion = 0;
        this.nombre = nombre;
    }

    public void aumentarConexion(Medium medium) {
        this.fortalecerConexion(medium.getCantFortalece());
        medium.agregarEspiritu(this);

    }

    public void fortalecerConexion(int cantidad) {
        this.nivelDeConexion = Math.min(this.limiteConexion(),this.nuevaConexion(cantidad));

    }

    private int nuevaConexion(int cantidad){
        return this.getNivelDeConexion() + cantidad;
    }

    private int limiteConexion() {
        return 100;
    }

    /*
    * Por ahora tanto espiritus como demonios tienen capacidad defensiva un random entre 1 al 100
    * */
    public int capacidadDefensiva() {
        return (int) RandomDefensa.numeroGenerado();
    }
}
}
