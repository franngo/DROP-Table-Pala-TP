package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Espiritu> espiritus;
    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Medium> mediums;

    public Ubicacion(String nombre) {

        this.nombre = nombre;
        this.espiritus = new HashSet<>();
        this.mediums = new HashSet<>();
    }

    public void agregarEspiritu(Espiritu espiritu) {
        this.espiritus.add(espiritu);
        espiritu.setUbicacion(this);
    }


    public boolean tieneElMismoNombreQue(Ubicacion ubicacion) {
        return this.getNombre() == ubicacion.getNombre();
    }

    public void agregarMedium(Medium medium) {
        this.mediums.add(medium);
        medium.setUbicacion(this);
    }

    public void salirDeUbicacion(Espiritu espiritu) {
        this.espiritus.remove(espiritu);
    }
}
