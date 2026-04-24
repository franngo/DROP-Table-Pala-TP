package ar.edu.unq.epersgeist.modelo.Espiritu;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
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
@DiscriminatorColumn(name = "tipo_espiritu")
public class Espiritu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private Integer nivelDeConexion;
    private String nombre;
    private boolean libre;
    public static final int limiteConexion = 100;


    @ManyToOne
    @JoinColumn(name = "mediumCon_id")
    private Medium mediumCon;
    @ManyToOne
    private Ubicacion ubicacion;


    public Espiritu(@NonNull String nombre) {
        this.nivelDeConexion = 0;
        this.nombre = nombre;
    }

    public void aumentarConexion(Medium medium) {
        this.fortalecerConexion(medium.getCantFortalece());
        medium.agregarEspiritu(this);

    }

    public void fortalecerConexion(int cantidad) {

        this.nivelDeConexion = Math.min(Espiritu.limiteConexion, this.cantidadNuevaConexion(cantidad));
    }


    private int cantidadNuevaConexion(int cantidad) {
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

