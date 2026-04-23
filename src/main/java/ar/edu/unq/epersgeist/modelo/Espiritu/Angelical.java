package ar.edu.unq.epersgeist.modelo.Espiritu;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor

@Entity
public class Angelical extends Espiritu {

    public Angelical(@NonNull String tipo, @NonNull String nombre) {
        super(tipo,nombre);
    }

}
