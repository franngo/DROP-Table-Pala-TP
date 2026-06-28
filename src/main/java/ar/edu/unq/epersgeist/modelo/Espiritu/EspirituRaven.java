package ar.edu.unq.epersgeist.modelo.Espiritu;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EspirituRaven {
    private String id;
    private Espiritu espiritu;
    private Coordenada coordenada;

    public EspirituRaven(Espiritu espiritu, Coordenada coordenada) {
        this.id = "espiritus-" + espiritu.getId().toString();
        this.espiritu = espiritu;
        this.coordenada = coordenada;
    }
}
