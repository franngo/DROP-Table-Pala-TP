package ar.edu.unq.epersgeist.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    public Ubicacion () {
        // Hibernate
    }

    public Ubicacion(String nombre) {
        this.nombre = nombre;
    }
}
