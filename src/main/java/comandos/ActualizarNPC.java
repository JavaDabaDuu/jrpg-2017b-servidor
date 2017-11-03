package comandos;

import java.io.IOException;

import mensajeria.PaqueteNPC;
import servidor.EscuchaCliente;
import servidor.Servidor;

/**
 * The Class ActualizarNPC.
 */
public class ActualizarNPC extends ComandosServer {

  /* (non-Javadoc)
   * @see mensajeria.Comando#ejecutar()
   */
  @Override
public void ejecutar() {
    getEscuchaCliente().setPaqueteNpc((PaqueteNPC) gson.fromJson(
        cadenaLeida, PaqueteNPC.class));
    Servidor.getNpcsActivos()
        .remove(getEscuchaCliente().getPaqueteNpc().getId());
    Servidor.getNpcsActivos().put(getEscuchaCliente()
        .getPaqueteNpc().getId(), getEscuchaCliente().getPaqueteNpc());

    for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
      try {
        conectado.getSalida()
            .writeObject(gson.toJson(getEscuchaCliente().getPaqueteNpc()));
      } catch (IOException e) {
        Servidor.getLog().append(
            "Falló al intentar enviar paqueteNPC a:"
              + conectado.getPaquetePersonaje().getId() + "\n");
      }
    }
  }
}
