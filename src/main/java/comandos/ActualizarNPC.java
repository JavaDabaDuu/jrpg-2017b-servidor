package comandos;

import java.io.IOException;

import mensajeria.PaqueteNPC;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ActualizarNPC extends ComandosServer {
  @Override
public void ejecutar() {
    escuchaCliente.setPaqueteNpc((PaqueteNPC) gson.fromJson(cadenaLeida, PaqueteNPC.class));
    Servidor.getNpcsActivos().remove(escuchaCliente.getPaqueteNpc().getId());
    Servidor.getNpcsActivos().put(escuchaCliente.getPaqueteNpc().getId(), 
        escuchaCliente.getPaqueteNpc());

    for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
      try {
        conectado.getSalida().writeObject(gson.toJson(escuchaCliente.getPaqueteNpc()));
      } catch (IOException e) {
        Servidor.log.append(
            "Falló al intentar enviar paqueteNPC a:"  
              + conectado.getPaquetePersonaje().getId() + "\n");
      }
    }
  }
}
