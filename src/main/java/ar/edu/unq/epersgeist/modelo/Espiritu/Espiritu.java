package ar.edu.unq.epersgeist.modelo.Espiritu;

import ar.edu.unq.epersgeist.modelo.Medium;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
public abstract class Espiritu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private Integer nivelDeConexion;
    private String nombre;
    private boolean libre;
    private static final int NIVEL_MAXIMO_CONEXION = 100;

    @ManyToOne
    @JoinColumn(name = "mediumCon_id")
    private Medium mediumCon;

    public Espiritu(@NonNull String nombre) {
        this.nivelDeConexion = 0;
        this.nombre = nombre;
    }

    public void aumentarConexion(Medium medium) {
        this.fortalecerConexion(medium.getCantFortalece());
        medium.agregarEspiritu(this);

    }

    public void fortalecerConexion(int cantidad) {
        this.nivelDeConexion = Math.min(NIVEL_MAXIMO_CONEXION, this.nuevaConexion(cantidad));

    }

    private int nuevaConexion(int cantidad) {
        return this.getNivelDeConexion() + cantidad;
    }


    public void desvincularse() {
        this.libre = true;
        this.mediumCon.desvincularseDe(this);
    }


    public boolean getLibre() {
        return libre;
    }


    public void recibirAtaqueDe(Espiritu angel) {
    }
}

