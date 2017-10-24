package servidor;

import java.util.HashMap;

import mensajeria.PaqueteNPC;

public class AtencionMovimientosNPC extends Thread {

	public AtencionMovimientosNPC() {

	}

	public void run() {
		HashMap<Integer, PaqueteNPC> npcsActivos = new HashMap<>();
		npcsActivos = Servidor.getNpcsActivos();

		while (true) {

			synchronized (this) {
				for (int i = 0; i < npcsActivos.size(); i++) {

					if (Servidor.getClientesConectados() != null) {

						for (EscuchaCliente clienteConectado : Servidor.getClientesConectados()) {
							while (clienteConectado.getPaqueteMovimiento() != null) {
								if (npcsActivos.get(i).estaEnRango(npcsActivos.get(i).getPosX(),
										npcsActivos.get(i).getPosY(),
										(int) (clienteConectado.getPaqueteMovimiento().getPosX()),
										(int) (clienteConectado.getPaqueteMovimiento().getPosY())))
								System.out.println("ESTAS EN RANGO");
							}
						}
					}

				}
			}
		}
	}
}
