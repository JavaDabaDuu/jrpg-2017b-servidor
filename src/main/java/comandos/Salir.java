package comandos;

import java.io.IOException;

import mensajeria.Paquete;
import servidor.Servidor;

/**
 * The Class Salir.
 */
public class Salir extends ComandosServer {

  /* (non-Javadoc)
   * @see mensajeria.Comando#ejecutar()
   */
  @Override
public void ejecutar() {
    // Cierro todo
    try {
      getEscuchaCliente().getEntrada().close();
      getEscuchaCliente().getSalida().close();
      getEscuchaCliente().getSocket().close();
    } catch (IOException e) {
      Servidor.getLog().append("Falló al intentar salir \n");

    }
    // Lo elimino de los clientes conectados
    Servidor.getClientesConectados().remove(this);
    Paquete paquete = (Paquete) gson.fromJson(cadenaLeida, Paquete.class);
    // Indico que se desconecto
    Servidor.getLog().append(paquete.getIp() + " se ha desconectado."
        + System.lineSeparator());
  }
}
