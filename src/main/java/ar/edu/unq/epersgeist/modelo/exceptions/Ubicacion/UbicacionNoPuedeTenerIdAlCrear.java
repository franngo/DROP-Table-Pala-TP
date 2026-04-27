package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

import ar.edu.unq.epersgeist.modelo.exceptions.EpergeistException;

public class UbicacionNoPuedeTenerIdAlCrear extends EpergeistException {
    public UbicacionNoPuedeTenerIdAlCrear() {
        super("No se puede crear una Ubicacion que ya tenga id.");
    }
}
