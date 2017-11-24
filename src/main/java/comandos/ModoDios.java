
package comandos;

import mensajeria.PaquetePersonaje;
import servidor.Servidor;

public class ModoDios extends ComandosServer{
	public void ejecutar() {
				PaquetePersonaje paquetePersonaje = (PaquetePersonaje)getGson().fromJson(getCadenaLeida(), PaquetePersonaje.class);
				Servidor.getPersonajesConectados().get(paquetePersonaje.getId()).activarModoDios();
				synchronized (Servidor.getAtencionConexiones()) {
					Servidor.getAtencionConexiones().notify();
				}
			}
}
