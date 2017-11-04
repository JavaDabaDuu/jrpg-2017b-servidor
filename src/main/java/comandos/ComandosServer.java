
package comandos;

import mensajeria.Comando;
import servidor.EscuchaCliente;

/**
 * The Class ComandosServer.
 */
public abstract class ComandosServer extends Comando {

  /** The escucha cliente. */
  private EscuchaCliente escuchaCliente;

  /**
   * Sets the escucha cliente.
   *
   * @param escuchaClienteAux the new escucha cliente
   */
  public void setEscuchaCliente(final EscuchaCliente escuchaClienteAux) {
    this.escuchaCliente = escuchaClienteAux;
  }

/**
 * Gets the escucha cliente.
 *
 * @return the escucha cliente
 */
public EscuchaCliente getEscuchaCliente() {
   return escuchaCliente;
}
}
