
package comandos;

import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import servidor.Servidor;

/**
 * The Class Conexion.
 */
public class Conexion extends ComandosServer {

  /* (non-Javadoc)
   * @see mensajeria.Comando#ejecutar()
   */
  @Override
public void ejecutar() {
    getEscuchaCliente().setPaquetePersonaje((PaquetePersonaje)
        (getGson().fromJson(getCadenaLeida(), PaquetePersonaje.class)).clone());

    Servidor.getPersonajesConectados().put(getEscuchaCliente()
        .getPaquetePersonaje().getId(), (PaquetePersonaje)
        getEscuchaCliente().getPaquetePersonaje().clone());
    Servidor.getUbicacionPersonajes().put(getEscuchaCliente()
        .getPaquetePersonaje().getId(), (PaqueteMovimiento)
        new PaqueteMovimiento(getEscuchaCliente().getPaquetePersonaje()
            .getId()).clone());
    synchronized (Servidor.getAtencionConexiones()) {
      Servidor.getAtencionConexiones().notify();
    }
  }
}
