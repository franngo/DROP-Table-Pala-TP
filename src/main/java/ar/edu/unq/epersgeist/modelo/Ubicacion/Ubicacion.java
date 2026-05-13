package ar.edu.unq.epersgeist.modelo.Ubicacion;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Min(0)
    @Max(100)
    @Column(nullable = false)
    private Integer energia;

    // --------------------------------------------------------------------------------

    public Ubicacion(String nombre, Integer energia) {
        this.nombre = nombre;
        this.energia = energia;
    }
    
    public abstract double dañoAEspiritu();

    public abstract int porcentajeDescansarMedium();

    public abstract int porcentajeDescansarAngelical();

    public abstract int porcentajeDescansarDemonio();

    public abstract boolean puedeInvocarseUnAngelical();

    public abstract boolean puedeInvocarseUnDemonio();

}
