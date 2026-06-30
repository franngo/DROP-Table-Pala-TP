package ar.edu.unq.epersgeist.modelo.Ubicacion;

import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request.TipoUbicacion;
import ar.edu.unq.epersgeist.modelo.Auditable;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipoUbicacion"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Santuario.class, name = "SANTUARIO"),
        @JsonSubTypes.Type(value = Cementerio.class, name = "CEMENTERIO")
})

// --------------------------------------------------------------------------------

public abstract class Ubicacion extends Auditable {
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

    public abstract int porcentajeDescansarMedium();

    public abstract int porcentajeDescansarAngelical();

    public abstract int porcentajeDescansarDemonio();

    public abstract boolean puedeInvocarseUnAngelical();

    public abstract boolean puedeInvocarseUnDemonio();

    public abstract TipoUbicacion getTipoUbicacion();


}
