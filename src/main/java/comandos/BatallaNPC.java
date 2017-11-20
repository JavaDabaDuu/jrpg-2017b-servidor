package comandos;

import java.io.IOException;

import estados.Estado;
import mensajeria.Comando;
import mensajeria.PaqueteBatallaNPC;
import mensajeria.PaqueteNPC;
import servidor.EscuchaCliente;
import servidor.Servidor;


/**
 * The Class BatallaNPC.
 */
public class BatallaNPC extends ComandosServer {

  /* (non-Javadoc)
   * @see mensajeria.Comando#ejecutar()
  */
  @Override
public void ejecutar() {
    getEscuchaCliente().setPaqueteBatallaNPC((PaqueteBatallaNPC)
        getGson().fromJson(getCadenaLeida(), PaqueteBatallaNPC.class));

    Servidor.getLog().append(getEscuchaCliente().getPaqueteBatallaNPC().getId()
        + " va a pelear con Enemigo " + getEscuchaCliente()
        .getPaqueteBatallaNPC().getIdEnemigo() + "\n");
    try {
      Servidor.getPersonajesConectados().get(getEscuchaCliente()
          .getPaqueteBatallaNPC().getId()).setEstado(Estado.getEstadoBatallaNPC());
      Servidor.getNpcsActivos().get(getEscuchaCliente().getPaqueteBatallaNPC()
          .getIdEnemigo()).setEstado(Estado.getEstadoBatallaNPC());
      getEscuchaCliente().getPaqueteBatallaNPC().setMiTurno(true);
      getEscuchaCliente().getSalida().writeObject(getGson().toJson(
          getEscuchaCliente().getPaqueteBatallaNPC()));
      
      PaqueteNPC paqueteNPC = Servidor.getNpcsActivos().get(getEscuchaCliente()
    		  							.getPaqueteBatallaNPC().getIdEnemigo());
     
      //Con esto mando el id del npc que tiene que desaparecer
      paqueteNPC.setComando(Comando.DESAPARECERNPC);

      for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
     	   try {
     		   //Por cada cliente conectado envio el id
                conectado.getSalida().writeObject(getGson()
                    .toJson(paqueteNPC));
                
             } catch (IOException e) {
              Servidor.getLog().append("Falló al intentar enviar"
              + "DesaparecerEnemigo a:"
              + conectado.getPaquetePersonaje().getId() + "\n");
             }
      } 
    } catch (Exception e) {
      Servidor.getLog().append("Falló al intentar enviar Batalla NPC \n");
    }
    synchronized (Servidor.getAtencionConexiones()) {
      Servidor.getAtencionConexiones().notify();
    }
  }

}
