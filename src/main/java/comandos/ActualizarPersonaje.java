package comandos;

import java.io.IOException;

import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

/**
 * The Class ActualizarPersonaje.
 */
public class ActualizarPersonaje extends ComandosServer {

  /* (non-Javadoc)
   * @see mensajeria.Comando#ejecutar()
   */
  @Override
public void ejecutar() {
    getEscuchaCliente().setPaquetePersonaje((PaquetePersonaje)
        getGson().fromJson(getCadenaLeida(), PaquetePersonaje.class));
    Servidor.getConector()
        .actualizarPersonaje(getEscuchaCliente().getPaquetePersonaje());
    Servidor.getPersonajesConectados()
        .remove(getEscuchaCliente().getPaquetePersonaje().getId());
    Servidor.getPersonajesConectados()
        .put(getEscuchaCliente().getPaquetePersonaje().getId(),
        getEscuchaCliente().getPaquetePersonaje());

    for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
      try {
        conectado.getSalida()
            .writeObject(getGson().toJson(getEscuchaCliente()
            .getPaquetePersonaje()));
      } catch (IOException e) {
        Servidor.getLog().append("Falló al intentar enviar paquetePersonaje a:"
            + conectado.getPaquetePersonaje().getId() + "\n");
      }
    }

  }

}
