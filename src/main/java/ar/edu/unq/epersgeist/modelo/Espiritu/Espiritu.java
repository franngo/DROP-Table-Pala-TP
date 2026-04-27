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

public abstract class Espiritu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double nivelDeConexion;
    private String nombre;


    @ManyToOne
    @JoinColumn(name = "mediumCon_id")
    private Medium mediumCon;
    @ManyToOne
    private Ubicacion ubicacion;

    public static final int limiteConexion = 100;

    public Espiritu(@NonNull String nombre) {
        this.nivelDeConexion = 0;
        this.nombre = nombre;

    }

    public boolean estaLibre() {
        return mediumCon == null;
    }

    public void aumentarConexionYConectar(Medium medium) {
        this.fortalecerConexion(medium.cantidadDeFortalecimientoDeConexion());
        medium.agregarEspiritu(this);
    }


    public void fortalecerConexion(double cantidad) {
        this.nivelDeConexion = Math.min(Espiritu.limiteConexion, this.cantidadNuevaConexion(cantidad));
    }


    private double cantidadNuevaConexion(double cantidad) {
        return this.getNivelDeConexion() + cantidad;
    }


    public void desvincularse() {

        this.mediumCon.desvincularseDe(this);
        this.mediumCon = null;
    }


    public abstract void recibirAtaqueDe(Espiritu angel);


    public boolean estaUbicadoEnElMismoLugarQue(Medium mediumAConectar) {
        return this.getUbicacion().tieneElMismoNombreQue(mediumAConectar.getUbicacion());
    }

    public void vincular(Medium medium) {
        this.mediumCon = medium;
    }

    public void salirDeUbicacionActual() {
        if (ubicacion != null) {
            this.ubicacion.salirDeUbicacion(this);
            this.ubicacion = null;
        }


    }
}

