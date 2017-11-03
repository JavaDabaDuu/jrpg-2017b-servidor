package comandos;

import java.io.IOException;

import mensajeria.PaqueteDeNPCS;
import mensajeria.PaqueteNPC;
import servidor.EscuchaCliente;
import servidor.Servidor;

/**
 * The Class SetearNPC.
 */
public class SetearNPC extends ComandosServer {

  /* (non-Javadoc)
   * @see mensajeria.Comando#ejecutar()
   */
  @Override
public void ejecutar() {
    PaqueteDeNPCS paqueteDeNpcs = (PaqueteDeNPCS) gson
        .fromJson(cadenaLeida, PaqueteDeNPCS.class);
    paqueteDeNpcs.getNpcs().putAll(Servidor.getNpcsActivos());
    try {
      getEscuchaCliente().getSalida()
          .writeObject(gson.toJson(paqueteDeNpcs, PaqueteDeNPCS.class));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
