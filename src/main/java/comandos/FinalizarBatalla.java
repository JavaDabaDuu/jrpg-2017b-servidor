package comandos;

import estados.Estado;

import java.io.IOException;
import mensajeria.PaqueteFinalizarBatalla;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class FinalizarBatalla extends ComandosServer {
  @Override
public void ejecutar() {
    PaqueteFinalizarBatalla paqueteFinalizarBatalla = (PaqueteFinalizarBatalla) 
        gson.fromJson(cadenaLeida, PaqueteFinalizarBatalla.class);
    escuchaCliente.setPaqueteFinalizarBatalla(paqueteFinalizarBatalla);
    Servidor.getConector().actualizarInventario(paqueteFinalizarBatalla.getGanadorBatalla());
    Servidor.getPersonajesConectados().get(escuchaCliente.getPaqueteFinalizarBatalla().getId())
        .setEstado(Estado.estadoJuego);
    Servidor.getPersonajesConectados().get(escuchaCliente.getPaqueteFinalizarBatalla()
        .getIdEnemigo()).setEstado(Estado.estadoJuego);
    for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
      if (conectado.getIdPersonaje() == escuchaCliente.getPaqueteFinalizarBatalla()
          .getIdEnemigo()) {
        try {
          conectado.getSalida().writeObject(gson.toJson(escuchaCliente
              .getPaqueteFinalizarBatalla()));
        } catch (IOException e) {
          Servidor.log.append("Falló al intentar enviar finalizarBatalla a:" + conectado
              .getPaquetePersonaje().getId() + "\n");
        }
      }
    }
    synchronized (Servidor.atencionConexiones) {
      Servidor.atencionConexiones.notify();
    }
  }
}
