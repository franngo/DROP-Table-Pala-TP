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

    // --------------------------------------------------------------------------------
    // Funcionalidad de conectarse
    // --------------------------------------------------------------------------------

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

    //usado para validar en el aumentarConexionYConectar() y en el invocar() de Medium
    public boolean estaLibre() {
        return mediumCon == null;
    }

    public boolean estaUbicadoEnElMismoLugarQue(Medium mediumAConectar) {
        return this.getUbicacion().equals(mediumAConectar.getUbicacion());
    }

    public void fortalecerConexion(double cantidad) {
        this.nivelDeConexion = Math.min(Espiritu.limiteConexion, this.cantidadNuevaConexion(cantidad));
    }

    private double cantidadNuevaConexion(double cantidad) {
        return this.getNivelDeConexion() + cantidad;
    }

    //usado en agregarEspiritu() de Medium
    public void vincular(Medium medium) {
        this.mediumCon = medium;
    }

    // --------------------------------------------------------------------------------
    // Funcionalidad de moverse
    // --------------------------------------------------------------------------------

    public void moverseA(Ubicacion ubiNueva) {
        this.setUbicacion(ubiNueva);
        this.cambiosConexion(getUbicacion());
    }

    public abstract void cambiosConexion(Ubicacion ubicacion);

    //usado en las implementaciones de cambiosConexion()
    public void afectarConexion(double cantidad) {
        this.nivelDeConexion = Math.max(0, this.getNivelDeConexion() - cantidad);
    }

    //usado en el mover() de Medium
    public boolean sinConexion() {
        return this.getNivelDeConexion() == 0;
    }

    //usado en el mover() y en el invocar() de Medium
    public void desvincularse() {
        this.mediumCon.desvincularseDe(this);
        this.mediumCon = null;
    }

    // --------------------------------------------------------------------------------
    // Funcionalidad de descansar
    // --------------------------------------------------------------------------------

    public void fortalecerConexionDescansandoEn(Ubicacion ubicacion) {
        int recarga = (int)(ubicacion.getEnergia() * this.porcentajeDescansarEn(ubicacion) / 100.0);
        this.fortalecerConexion(recarga);
    }

    //double dispatch para el fortalecerConexionDescansandoEn(), usado en el descansar() de Medium
    public abstract int porcentajeDescansarEn(Ubicacion ubicacion);

    // --------------------------------------------------------------------------------
    // Subtareas
    // --------------------------------------------------------------------------------

    //double dispatch para el invocar() de Medium
    public abstract boolean puedeInvocarseEn(Ubicacion ubicacion);

    //usado en la ronda de combate del exorcizar() de Medium
    public abstract void recibirAtaqueDe(Espiritu angel);

}
