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
    PaqueteDeNPCS paqueteDeNpcs = (PaqueteDeNPCS) getGson()
        .fromJson(getCadenaLeida(), PaqueteDeNPCS.class);
    paqueteDeNpcs.getNpcs().putAll(Servidor.getNpcsActivos());
    try {
      getEscuchaCliente().getSalida()
          .writeObject(getGson().toJson(paqueteDeNpcs, PaqueteDeNPCS.class));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
