package comandos;

import java.util.Iterator;
import estados.Estado;
import mensajeria.PaqueteFinalizarBatallaNPC;
import mensajeria.PaqueteMovimiento;
import servidor.Servidor;

/**
 * The Class FinalizarBatallaNPC.
 */
public class FinalizarBatallaNPC extends ComandosServer {

	/* (non-Javadoc)
	 * @see mensajeria.Comando#ejecutar()
	 */
	@Override
	public void ejecutar() {
		// TODO Auto-generated method stub

		PaqueteFinalizarBatallaNPC paqueteFinalizarBatalla = (PaqueteFinalizarBatallaNPC)getGson().fromJson(getCadenaLeida(), PaqueteFinalizarBatallaNPC.class); 
		getEscuchaCliente().setPaqueteFinalizarBatallaNPC(paqueteFinalizarBatalla);
		Servidor.getPersonajesConectados().get(getEscuchaCliente().getPaqueteFinalizarBatallaNPC().getId()).setEstado(Estado.getEstadoJuego());
		float x = 0;
		float y = 0;
		boolean agrego = false;
		while (!agrego) {
			Iterator<Integer> it = Servidor.getUbicacionPersonajes().keySet().iterator();
			int key;
			PaqueteMovimiento actual;
			x = (float)Math.random()*(0-1500)+1500;
			y = (float)Math.random()*(x/2-1600)+1600;
			boolean personajeCerca = false;
			while (it.hasNext() && !personajeCerca) {
			key = it.next();
			actual = Servidor.getUbicacionPersonajes().get(key);
			if (actual != null) {
				if( Math.sqrt(Math.pow(actual.getPosX() - x, 2) + Math.pow(actual.getPosY() - y, 2))<=500)
					personajeCerca=true;
				}
			}
				if(personajeCerca==false)
					agrego=true;
		}
			if(agrego) {
				int id_enemigo = getEscuchaCliente().getPaqueteFinalizarBatallaNPC().getIdEnemigo();
				Servidor.getNpcsActivos().get(id_enemigo).setPosX(x);
				Servidor.getNpcsActivos().get(id_enemigo).setPosY(y);
				Servidor.getNpcsActivos().get(id_enemigo).setEstado(Estado.getEstadoJuego());
			}
		synchronized(Servidor.getAtencionConexiones()) {
		Servidor.getAtencionConexiones().notify();
				}
			}
	}

