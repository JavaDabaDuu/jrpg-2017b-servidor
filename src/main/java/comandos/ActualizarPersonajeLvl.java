package comandos;

import java.io.IOException;

import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

/**
 * The Class ActualizarPersonajeLvl.
 */
public class ActualizarPersonajeLvl extends ComandosServer {

  /* (non-Javadoc)
   * @see mensajeria.Comando#ejecutar()
   */
  @Override
public void ejecutar() {
    getEscuchaCliente().setPaquetePersonaje((PaquetePersonaje)
        gson.fromJson(cadenaLeida, PaquetePersonaje.class));
    Servidor.getConector()
        .actualizarPersonajeSubioNivel(
        getEscuchaCliente().getPaquetePersonaje());
    Servidor.getPersonajesConectados()
        .remove(getEscuchaCliente().getPaquetePersonaje().getId());
    Servidor.getPersonajesConectados()
        .put(getEscuchaCliente().getPaquetePersonaje().getId(),
        getEscuchaCliente().getPaquetePersonaje());
    getEscuchaCliente().getPaquetePersonaje().ponerBonus();
    getEscuchaCliente().getPaquetePersonaje().actualizarPuntosPorNivel();
    for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
      try {
        conectado.getSalida()
            .writeObject(gson
            .toJson(getEscuchaCliente().getPaquetePersonaje()));
      } catch (IOException e) {
        Servidor.getLog().append("Falló al intentar enviar paquetePersonaje a:"
            + conectado.getPaquetePersonaje().getId() + "\n");
      }
    }
  }
}
