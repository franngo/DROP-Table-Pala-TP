package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Espiritu> espiritus = new ArrayList<>();

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Medium> medium = new ArrayList<>();


    public Ubicacion () {
        // Hibernate
    }

    public Ubicacion(String nombre) {
        this.nombre = nombre;
    }

    public void agregarEspiritu(Espiritu espiritu) {
        this.espiritus.add(espiritu);
    }

    public void agregarMedium(Medium medium) {
        this.medium.add(medium);
    }
}
