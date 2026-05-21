package ar.edu.unq.epersgeist.modelo.Espiritu;

import ar.edu.unq.epersgeist.modelo.Auditable;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlConectarEspirituConMedium;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionInvalidaException;
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

public abstract class Espiritu extends Auditable implements Serializable {


    public static final int limiteConexion = 100;
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

    public Espiritu(@NonNull String nombre, Ubicacion ubicacion) {
        this.validarUbicacionNoNula(ubicacion);

        this.nivelDeConexion = 0;
        this.nombre = nombre;
        this.ubicacion = ubicacion;

    }

    private void validarUbicacionNoNula(Ubicacion ubicacion) {
        if (ubicacion == null) {
            throw new UbicacionInvalidaException();
        }
    }

    public boolean estaLibre() {
        return mediumCon == null;
    }

    public void aumentarConexionYConectar(Medium medium) {
        if (!this.estaLibre()) {
            throw new ErrorAlConectarEspirituConMedium("El espiritu no se encuentra libre");
        }
        if (!this.estaUbicadoEnElMismoLugarQue(medium)) {
            throw new ErrorAlConectarEspirituConMedium("El espiritu se encuentra en una ubicacion diferente al medium");
        }

        this.fortalecerConexion(medium.cantidadDeFortalecimientoDeConexion());
        medium.agregarEspiritu(this);
    }


    public void fortalecerConexion(double cantidad) {
        this.nivelDeConexion = Math.min(Espiritu.limiteConexion, this.cantidadNuevaConexion(cantidad));
    }

    public void afectarConexion(double cantidad) {
        this.nivelDeConexion = Math.max(0, this.getNivelDeConexion() - cantidad);
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
        return this.getUbicacion().equals(mediumAConectar.getUbicacion());
    }

    public void vincular(Medium medium) {
        this.mediumCon = medium;
    }

    public void moverseA(Ubicacion ubiNueva) {

        this.setUbicacion(ubiNueva);
        this.cambiosConexion(getUbicacion());
    }

    public boolean sinConexion() {
        return this.getNivelDeConexion() == 0;
    }

    public abstract void cambiosConexion(Ubicacion ubicacion);

    public abstract int porcentajeDescansarEn(Ubicacion ubicacion);

    public void fortalecerConexionDescansandoEn(Ubicacion ubicacion) {
        int recarga = (int)(ubicacion.getEnergia() * this.porcentajeDescansarEn(ubicacion) / 100.0);
        this.fortalecerConexion(recarga);
    }

    public abstract boolean puedeInvocarseEn(Ubicacion ubicacion);

}
