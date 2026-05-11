package ar.edu.unq.epersgeist.modelo.Ubicacion;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// --------------------------------------------------------------------------------

@Getter
@Setter
@NoArgsConstructor

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_ubicacion")

// --------------------------------------------------------------------------------

public abstract class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Espiritu> espiritus = new ArrayList<>();

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Medium> mediums = new ArrayList<>();

    // --------------------------------------------------------------------------------

    public Ubicacion(String nombre) {
        this.nombre = nombre;
    }

    // --------------------------------------------------------------------------------

    public void agregarEspiritu(Espiritu espiritu) {
        if (!espiritus.contains(espiritu)) {
            espiritus.add(espiritu);
            espiritu.setUbicacion(this);
        }
    }

    public void removerEspiritu(Espiritu espiritu) {
        if (espiritus.contains(espiritu)) {
            espiritus.remove(espiritu);
            espiritu.setUbicacion(null);
        }
    }

    public boolean contieneEspiritu(Espiritu espiritu) {
        return espiritus.contains(espiritu);
    }

    // --------------------------------------------------------------------------------

    public void agregarMedium(Medium medium) {
        if (!mediums.contains(medium)) {
            mediums.add(medium);
            medium.setUbicacion(this);
        }
    }

    public void removerMedium(Medium medium) {
        if (mediums.contains(medium)) {
            mediums.remove(medium);
            medium.setUbicacion(null);
        }
    }

    public boolean contieneMedium(Medium medium) {
        return mediums.contains(medium);
    }

    // --------------------------------------------------------------------------------

    public abstract void aplicarEfectoMovimiento(Espiritu espiritu);

    // --------------------------------------------------------------------------------
}