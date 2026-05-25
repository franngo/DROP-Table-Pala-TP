package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

import ar.edu.unq.epersgeist.modelo.exceptions.EpergeistException;

public class UbicacionesNoConectadasException extends EpergeistException {
    public UbicacionesNoConectadasException(Long idOrigen, Long idDestino) {
        super(String.format("Las ubicaciones con ID %d y %d no están conectadas.", idOrigen, idDestino));
    }
}
