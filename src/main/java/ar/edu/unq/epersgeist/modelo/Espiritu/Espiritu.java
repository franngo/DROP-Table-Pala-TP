package ar.edu.unq.epersgeist.modelo.Espiritu;

import ar.edu.unq.epersgeist.controller.dtos.espiritus.request.TipoEspiritu;
import ar.edu.unq.epersgeist.modelo.Auditable;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDominacionInvalidaCandidatoLoDominaException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDominacionInvalidaCandidatoNoAptoException;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorDominacionInvalidaPorDistanciaException;
import ar.edu.unq.epersgeist.modelo.medium.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.exceptions.ErrorAlConectarEspirituConMedium;
import ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion.UbicacionInvalidaException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Ubicacion ubicacion;
    @ManyToOne
    @JoinColumn(name = "espirituDominante_id")
    private Espiritu espirituDominante;

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
    // Funcionalidad de conectarse con un Medium
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

    //efectua los cambios correspondientes sobre el nivel de conexión del Espiritu en base a qué tipo de Ubicación se traslada
    public abstract void cambiosConexion(Ubicacion ubicacion);

    //usado en las implementaciones de cambiosConexion()
    public void afectarConexion(double cantidad) {
        this.nivelDeConexion = Math.max(0, this.getNivelDeConexion() - cantidad);
    }

    //usado en el mover() de Medium
    public boolean sinConexion() {
        return this.getNivelDeConexion() == 0;
    }

    // --------------------------------------------------------------------------------
    // Funcionalidad de descansar
    // --------------------------------------------------------------------------------

    public void fortalecerConexionDescansandoEn(Ubicacion ubicacion) {
        int recarga = this.porcentajeDescansarEn(ubicacion) * (int)(ubicacion.getEnergia() / 100.0);
        this.fortalecerConexion(recarga);
    }

    //double dispatch para el fortalecerConexionDescansandoEn(), usado en el descansar() de Medium
    public abstract int porcentajeDescansarEn(Ubicacion ubicacion);

    // --------------------------------------------------------------------------------
    // Funcionalidad de dominar
    // --------------------------------------------------------------------------------

    public void dominar(Espiritu espirituADominar, double distanciaEspiritus) {
        this.validarDominacion(espirituADominar, distanciaEspiritus);
    }

    private void validarDominacion(Espiritu espirituADominar, double distanciaEspiritus) {
        if(!(distanciaEspiritus >= 2000.0 && distanciaEspiritus <= 5000.0)) {
            throw new ErrorDominacionInvalidaPorDistanciaException(this.getId(), espirituADominar.getId(), distanciaEspiritus);
        }

        if(!espirituADominar.estaLibre() || espirituADominar.getNivelDeConexion() >= 50 || espirituADominar.estaDominado()) {
            throw new ErrorDominacionInvalidaCandidatoNoAptoException(this.getId(), espirituADominar.getId());
        }

//        if(this.estaDominado() && espirituADominar.getId().equals(this.espirituDominante.getId())) {
//            throw new ErrorDominacionInvalidaCandidatoLoDominaException(this.getId(), espirituADominar.getId());
//        }

        espirituADominar.efectuarDominanciaPor(this);
    }

    public boolean estaDominado() {
        return espirituDominante != null;
    }

    public void efectuarDominanciaPor(Espiritu espirituDominante) {
        this.espirituDominante = espirituDominante;
    }

    // --------------------------------------------------------------------------------
    // Subtareas
    // --------------------------------------------------------------------------------

    //usado en el mover() de Medium y en la batalla dada en el exorcizar() de Medium en los casos donde el Espiritu queda con nivel de conexión 0
    public void desvincularse() {
        this.mediumCon.desvincularseDe(this);
        this.mediumCon = null;
    }

    //double dispatch para el invocar() de Medium
    public abstract boolean puedeInvocarseEn(Ubicacion ubicacion);

    //usado en la ronda de combate del exorcizar() de Medium
    public abstract void recibirAtaqueDe(Espiritu espiritu);

    abstract public TipoEspiritu getTipoEspiritu();

}
