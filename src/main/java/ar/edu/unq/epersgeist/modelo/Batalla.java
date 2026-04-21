package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.Espiritu.Angelical;
import ar.edu.unq.epersgeist.modelo.Espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.Espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.random.Random;

import java.util.ArrayList;
import java.util.List;

public class Batalla {
    boolean ganoAtacante;
    Medium atacante;
    Medium atacado;
    public Batalla(Medium medium, Medium mediumAExorcizar) {
        this.atacante=medium;
        this.atacado=mediumAExorcizar;
    }

    void iniciarBatalla(){

        //angeles medium que ataca
       List<Angelical> angelesAtacante = atacante.espiritusAngelicales();
       List<Demonio> demoniosAtacado = new ArrayList<>(atacado.espiritusDemonios());

        int indexDemonio = 0;
       for (Angelical angel : angelesAtacante){

           Demonio demonioAtacado = demoniosAtacado.get(indexDemonio);
           if (demoniosAtacado.isEmpty()) {
               this.ganoAtacante=false;
               break;
           }

           int porcentajeDeEXITO = this.porcentajeDeAtaqueExitoso(angel);
           int defensaDemonio = demonioAtacado.capacidadDefensiva();

           if(porcentajeDeEXITO>defensaDemonio){
               demonioAtacado.recibirAtaque();
               if(!demonioAtacado.estaActivo()){
                   demoniosAtacado.remove(indexDemonio);
               }
           }
           else {
               angel.recibirAtaque();
           }



           indexDemonio++;
       }

    }

    public int cantidadAPerder(){
        return 0;
    }

    public int porcentajeDeAtaqueExitoso(Espiritu espiritu){
        return (int) Math.max(Random.numeroGenerado() + espiritu.getNivelDeConexion(),100);
    }



    public void accionesPostBatalla() {
        if(this.ganoAtacante){
            atacado.desvincularDemonios();
        }
        else{
            atacante.desvincularAngeles();
        }
    }
}
