package ar.edu.unq.epersgeist.modelo.exceptions.Ubicacion;

public class UbicacionNoPuedeTenerIdAlCrear extends RuntimeException {
    public UbicacionNoPuedeTenerIdAlCrear() {
        super("No se puede crear una Ubicacion que ya tenga id.");
    }
}
