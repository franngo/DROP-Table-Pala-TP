package ar.edu.unq.epersgeist.controller.dtos.ubicaciones.request;

import ar.edu.unq.epersgeist.modelo.Coordenada;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoordenadaDTO {
    private Double latitud;
    private Double longitud;

    public Coordenada aModelo() {
        return new Coordenada(
                latitud,
                longitud
        );
    }
}
