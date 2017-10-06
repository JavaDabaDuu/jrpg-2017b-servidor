package comandos;

import mensajeria.PaqueteNPC;
import servidor.Servidor;

public class SetearNPC extends ComandosServer {

	@Override
	public void ejecutar() {
		escuchaCliente.setPaqueteNpc((PaqueteNPC) gson.fromJson(cadenaLeida, PaqueteNPC.class));
		Servidor.getNpcsActivos().add(escuchaCliente.getPaqueteNpc());
		
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}
	}

}
