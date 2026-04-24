package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    private List<Espiritu> espiritus = new ArrayList<Espiritu>();
    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Medium> mediums = new ArrayList<Medium>();

    public Ubicacion(String nombre) {
        this.nombre = nombre;
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
}
