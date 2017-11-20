
package comandos;

import estados.Estado;

import java.io.IOException;
import mensajeria.PaqueteFinalizarBatalla;
import servidor.EscuchaCliente;
import servidor.Servidor;

/**
 * The Class FinalizarBatalla.
 */
public class FinalizarBatalla extends ComandosServer {
  /* (non-Javadoc)
   * @see mensajeria.Comando#ejecutar()
   */
  @Override
public void ejecutar() {
    PaqueteFinalizarBatalla paqueteFinalizarBatalla = (PaqueteFinalizarBatalla)
        getGson().fromJson(getCadenaLeida(), PaqueteFinalizarBatalla.class);
    getEscuchaCliente().setPaqueteFinalizarBatalla(paqueteFinalizarBatalla);
    Servidor.getConector().actualizarInventario(
        paqueteFinalizarBatalla.getGanadorBatalla());
  
    if(Servidor.getPersonajesConectados().get(getEscuchaCliente().getPaqueteFinalizarBatalla().getId()).getEstado() == Estado.getEstadoBatalla()) {    	  
    	    Servidor.getPersonajesConectados().get(getEscuchaCliente().
    	    		getPaqueteFinalizarBatalla().getIdEnemigo()).
    	    setEstado(Estado.getEstadoJuego());    	
    }else {  	
    	 Servidor.getNpcsActivos().get(getEscuchaCliente()
    	            .getPaqueteFinalizarBatalla()
    	            .getIdEnemigo()).setEstado	(Estado.getEstadoJuego());
    }

	  Servidor.getPersonajesConectados().get(getEscuchaCliente()
		        .getPaqueteFinalizarBatalla().getId())
		        .setEstado(Estado.getEstadoJuego());
   
    for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
      if (conectado.getIdPersonaje() == getEscuchaCliente()
          .getPaqueteFinalizarBatalla().getIdEnemigo()) {
        try {
          conectado.getSalida().writeObject(getGson().toJson(getEscuchaCliente()
              .getPaqueteFinalizarBatalla()));
        } catch (IOException e) {
          Servidor.getLog().append(
              "Falló al intentar enviar finalizarBatalla a:"
              + conectado.getPaquetePersonaje().getId() + "\n");
        }
      }
    }
    synchronized (Servidor.getAtencionConexiones()) {
      Servidor.getAtencionConexiones().notify();
    }
  }
}
