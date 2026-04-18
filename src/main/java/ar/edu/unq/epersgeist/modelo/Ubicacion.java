package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Entity
public class Ubicacion {
    @Id
    Long id;
    @Column(unique = true)
    String nombre;
    List<Espiritu> espiritus;
    List<Medium> mediums;
}
