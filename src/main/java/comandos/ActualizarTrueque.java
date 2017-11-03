package comandos;

import java.io.IOException;

import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

/**
 * The Class ActualizarTrueque.
 */
public class ActualizarTrueque extends ComandosServer {

  /* (non-Javadoc)
   * @see mensajeria.Comando#ejecutar()
   */
  @Override
public void ejecutar() {
    getEscuchaCliente().setPaquetePersonaje((PaquetePersonaje)
        gson.fromJson(cadenaLeida, PaquetePersonaje.class));
    Servidor.getConector()
        .actualizarInventario(getEscuchaCliente().getPaquetePersonaje());
    Servidor.getConector()
        .actualizarPersonaje(getEscuchaCliente().getPaquetePersonaje());
    Servidor.getPersonajesConectados()
        .remove(getEscuchaCliente().getPaquetePersonaje().getId());
    Servidor.getPersonajesConectados()
        .put(getEscuchaCliente().getPaquetePersonaje().getId(),
        getEscuchaCliente().getPaquetePersonaje());

    for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
      try {
        conectado.getSalida().writeObject(gson.toJson(
            getEscuchaCliente().getPaquetePersonaje()));
      } catch (IOException e) {
        Servidor.getLog()
           .append("Falló al intentar enviar actualizacion de trueque a:"
           + conectado.getPaquetePersonaje().getId() + "\n");
      }
    }

  }

}
