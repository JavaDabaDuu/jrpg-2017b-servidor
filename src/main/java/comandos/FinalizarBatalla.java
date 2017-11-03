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
        gson.fromJson(cadenaLeida, PaqueteFinalizarBatalla.class);
    getEscuchaCliente().setPaqueteFinalizarBatalla(paqueteFinalizarBatalla);
    Servidor.getConector().actualizarInventario(
        paqueteFinalizarBatalla.getGanadorBatalla());
    Servidor.getPersonajesConectados().get(getEscuchaCliente()
        .getPaqueteFinalizarBatalla().getId())
        .setEstado(Estado.estadoJuego);
    Servidor.getPersonajesConectados().get(getEscuchaCliente()
        .getPaqueteFinalizarBatalla()
        .getIdEnemigo()).setEstado(Estado.estadoJuego);
    for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
      if (conectado.getIdPersonaje() == getEscuchaCliente()
          .getPaqueteFinalizarBatalla().getIdEnemigo()) {
        try {
          conectado.getSalida().writeObject(gson.toJson(getEscuchaCliente()
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
