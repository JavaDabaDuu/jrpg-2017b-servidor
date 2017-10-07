package servidor;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

import comandos.ComandosServer;
import juego.Pantalla;
import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteAtacar;
import mensajeria.PaqueteBatalla;
import mensajeria.PaqueteDeMovimientos;
import mensajeria.PaqueteDeNPCS;
import mensajeria.PaqueteDePersonajes;
import mensajeria.PaqueteFinalizarBatalla;
import mensajeria.PaqueteMovimiento;
import mensajeria.PaqueteNPC;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;

public class EscuchaCliente extends Thread {

	private final Socket socket;
	private final ObjectInputStream entrada;
	private final ObjectOutputStream salida;
	private int idPersonaje;
	private final Gson gson = new Gson();

	private PaquetePersonaje paquetePersonaje;
	private PaqueteMovimiento paqueteMovimiento;
	private PaqueteBatalla paqueteBatalla;
	private PaqueteAtacar paqueteAtacar;
	private PaqueteFinalizarBatalla paqueteFinalizarBatalla;
	private PaqueteUsuario paqueteUsuario;
	private PaqueteDeMovimientos paqueteDeMovimiento;
	private PaqueteDePersonajes paqueteDePersonajes;
	private PaqueteNPC paqueteNpc;
	private PaqueteDeNPCS paqueteDeNpcs;
	private final static int CANTIDADNPCS = 10;
	private final static String TIPONPC = "Minotauro";

	public EscuchaCliente(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida)
			throws IOException {
		this.socket = socket;
		this.entrada = entrada;
		this.salida = salida;
		paquetePersonaje = new PaquetePersonaje();
		paqueteNpc = new PaqueteNPC();
	}

	public void run() {
		try {
			ComandosServer comand;
			Paquete paquete;
			Paquete paqueteSv = new Paquete(null, 0);
			paqueteUsuario = new PaqueteUsuario();

			String cadenaLeida = (String) entrada.readObject();
			this.dibujarMinotauros();

			while (!((paquete = gson.fromJson(cadenaLeida, Paquete.class)).getComando() == Comando.DESCONECTAR)) {
				comand = (ComandosServer) paquete.getObjeto(Comando.NOMBREPAQUETE);
				comand.setCadena(cadenaLeida);
				comand.setEscuchaCliente(this);
				comand.ejecutar();
				cadenaLeida = (String) entrada.readObject();
			}

			entrada.close();
			salida.close();
			socket.close();

			Servidor.getPersonajesConectados().remove(paquetePersonaje.getId());
			Servidor.getUbicacionPersonajes().remove(paquetePersonaje.getId());
			Servidor.getClientesConectados().remove(this);

			for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
				paqueteDePersonajes = new PaqueteDePersonajes(Servidor.getPersonajesConectados());
				paqueteDePersonajes.setComando(Comando.CONEXION);
				conectado.salida.writeObject(gson.toJson(paqueteDePersonajes, PaqueteDePersonajes.class));
			}

			Servidor.log.append(paquete.getIp() + " se ha desconectado." + System.lineSeparator());

		} catch (IOException | ClassNotFoundException e) {
			Servidor.log.append("Error de conexion: " + e.getMessage() + System.lineSeparator());
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public ObjectInputStream getEntrada() {
		return entrada;
	}

	public ObjectOutputStream getSalida() {
		return salida;
	}

	public PaquetePersonaje getPaquetePersonaje() {
		return paquetePersonaje;
	}

	public int getIdPersonaje() {
		return idPersonaje;
	}

	public PaqueteMovimiento getPaqueteMovimiento() {
		return paqueteMovimiento;
	}

	public void setPaqueteMovimiento(PaqueteMovimiento paqueteMovimiento) {
		this.paqueteMovimiento = paqueteMovimiento;
	}

	public PaqueteBatalla getPaqueteBatalla() {
		return paqueteBatalla;
	}

	public void setPaqueteBatalla(PaqueteBatalla paqueteBatalla) {
		this.paqueteBatalla = paqueteBatalla;
	}

	public PaqueteAtacar getPaqueteAtacar() {
		return paqueteAtacar;
	}

	public void setPaqueteAtacar(PaqueteAtacar paqueteAtacar) {
		this.paqueteAtacar = paqueteAtacar;
	}

	public PaqueteFinalizarBatalla getPaqueteFinalizarBatalla() {
		return paqueteFinalizarBatalla;
	}

	public void setPaqueteFinalizarBatalla(PaqueteFinalizarBatalla paqueteFinalizarBatalla) {
		this.paqueteFinalizarBatalla = paqueteFinalizarBatalla;
	}

	public PaqueteDeMovimientos getPaqueteDeMovimiento() {
		return paqueteDeMovimiento;
	}

	public void setPaqueteDeMovimiento(PaqueteDeMovimientos paqueteDeMovimiento) {
		this.paqueteDeMovimiento = paqueteDeMovimiento;
	}

	public PaqueteDePersonajes getPaqueteDePersonajes() {
		return paqueteDePersonajes;
	}

	public void setPaqueteDePersonajes(PaqueteDePersonajes paqueteDePersonajes) {
		this.paqueteDePersonajes = paqueteDePersonajes;
	}

	public void setIdPersonaje(int idPersonaje) {
		this.idPersonaje = idPersonaje;
	}

	public void setPaquetePersonaje(PaquetePersonaje paquetePersonaje) {
		this.paquetePersonaje = paquetePersonaje;
	}

	public PaqueteUsuario getPaqueteUsuario() {
		return paqueteUsuario;
	}

	public void setPaqueteUsuario(PaqueteUsuario paqueteUsuario) {
		this.paqueteUsuario = paqueteUsuario;
	}

	public static void inicializarNPCS() {
		int posIniX = 800;
		int posIniY = 1041;

		int decrementoX = 405;
		int incrementoY = 150;
		for (int i = 0; i < CANTIDADNPCS; i++) {// quedan estas cuentas raras
												// para que queden las
												// ubicaciones mas o menos como
												// las habian puesto los chicos
			if (i == 0)
				Servidor.getNpcsActivos().put(i, new PaqueteNPC(i, "Minotauro" + i, TIPONPC, 1, 1, posIniX, posIniY));
			if (i < 7)
				Servidor.getNpcsActivos().put(i, new PaqueteNPC(i, "Minotauro" + i, TIPONPC, 1, 1,
						posIniX - decrementoX, posIniY + incrementoY));

			Servidor.getNpcsActivos().put(i,
					new PaqueteNPC(i, "Minotauro" + i, TIPONPC, 1, 1, posIniX - decrementoX, posIniY - incrementoY));
		}
	}

	public PaqueteNPC getPaqueteNpc() {
		return paqueteNpc;
	}

	public void setPaqueteNpc(PaqueteNPC paqueteNpc) {
		this.paqueteNpc = paqueteNpc;
	}

	public void dibujarMinotauros() {
		paqueteDeNpcs = new PaqueteDeNPCS(Servidor.getNpcsActivos());
		paqueteDeNpcs.setComando(Comando.SETEARNPCS);
		try {
			this.salida.writeObject(gson.toJson(paqueteDeNpcs, PaqueteDeNPCS.class));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
