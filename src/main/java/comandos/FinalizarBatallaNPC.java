package comandos;

import java.util.Iterator;

import estados.Estado;
import mensajeria.PaqueteFinalizarBatallaNPC;
import mensajeria.PaqueteMovimiento;
import servidor.Servidor;

public class FinalizarBatallaNPC extends ComandosServer{

	private static final int RANGOFINALX = 1500;
	private static final int RANGOFINALY = 1600;
	private static final int RANGODEATAQUE = 500;
	
	/**
	 * Este metodo ejecuta el respawn del NPC
	 */
	
	@Override
	public void ejecutar() {
		
		 PaqueteFinalizarBatallaNPC paqueteFinalizarBatalla =
	                (PaqueteFinalizarBatallaNPC) gson.
	                fromJson(cadenaLeida, PaqueteFinalizarBatallaNPC.class);
	                escuchaCliente.setPaqueteFinalizarBatallaNPC(
	                paqueteFinalizarBatalla);
	        Servidor.getPersonajesConectados().get(
	                escuchaCliente.getPaqueteFinalizarBatallaNPC().getId())
	                .setEstado(Estado.estadoJuego);
	        // VER SI TIENE PERSONAJES CERCA
	        float x = 0;
	        float y = 0;
	        boolean agrego = false;
	        while (!agrego) {
	            Iterator<Integer> it = Servidor.getUbicacionPersonajes()
	                .keySet().iterator();
	            int key;
	            PaqueteMovimiento actual;
	            x = (float) Math.random()
	                * (0 - RANGOFINALX) + RANGOFINALX;
	            y = (float) Math.random() * (x / 2 - RANGOFINALY) + RANGOFINALY;
	            boolean personajeCerca = false;
	            while (it.hasNext() && !personajeCerca) {
	                key = it.next();
	            actual = Servidor.getUbicacionPersonajes().get(key);
	                if (actual != null) {
	                     if (Math.sqrt(Math.pow(actual.getPosX() - x, 2)
	                        + Math.pow(actual.getPosY() - y, 2)) <= RANGODEATAQUE) {
	                        personajeCerca = true;
	                     }
	                }
	            }
	            if (!personajeCerca) {
	                agrego = true;
	            }
	        }
	        if (agrego) {
	            int idEnemigo = escuchaCliente.
	                  getPaqueteFinalizarBatallaNPC().getIdEnemigo();
	            Servidor.getNpcsActivos().get(idEnemigo).setPosX(x);
	            Servidor.getNpcsActivos().get(idEnemigo).setPosY(y);
	            Servidor.getNpcsActivos().get(idEnemigo).setEstado(Estado.estadoJuego);
	        }
		synchronized (Servidor.atencionConexiones) {
			Servidor.atencionConexiones.notify();
	        }
	     
		
	}

}
