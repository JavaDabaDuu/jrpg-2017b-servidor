
package servidor;

import com.google.gson.Gson;
import comandos.ComandosServer;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;

import javax.imageio.ImageIO;

import juego.Pantalla;
import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteAtacar;
import mensajeria.PaqueteBatalla;
import mensajeria.PaqueteBatallaNPC;
import mensajeria.PaqueteDeMovimientos;
import mensajeria.PaqueteDeNPCS;
import mensajeria.PaqueteDePersonajes;
import mensajeria.PaqueteFinalizarBatalla;
import mensajeria.PaqueteMovimiento;
import mensajeria.PaqueteNPC;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;

/**
 * The Class EscuchaCliente.
 */
public class EscuchaCliente extends Thread {

  /** The socket. */
  private final Socket socket;

  /** The entrada. */
  private final ObjectInputStream entrada;

  /** The salida. */
  private final ObjectOutputStream salida;

  /** The id personaje. */
  private int idPersonaje;

  /** The gson. */
  private final Gson gson = new Gson();

  /** The paquete personaje. */
  private PaquetePersonaje paquetePersonaje;

  /** The paquete movimiento. */
  private PaqueteMovimiento paqueteMovimiento;

  /** The paquete batalla. */
  private PaqueteBatalla paqueteBatalla;

  /** The paquete atacar. */
  private PaqueteAtacar paqueteAtacar;

  /** The paquete batalla NPC. */
  private PaqueteBatallaNPC paqueteBatallaNPC;

  /** The paquete finalizar batalla. */
  private PaqueteFinalizarBatalla paqueteFinalizarBatalla;

  /** The paquete usuario. */
  private PaqueteUsuario paqueteUsuario;

  /** The paquete de movimiento. */
  private PaqueteDeMovimientos paqueteDeMovimiento;

  /** The paquete de personajes. */
  private PaqueteDePersonajes paqueteDePersonajes;

  /** The paquete npc. */
  private PaqueteNPC paqueteNpc;

  /** The paquete de npcs. */
  private PaqueteDeNPCS paqueteDeNpcs;

  /** The Constant CANTIDADNPCS. */
  private static final int CANTIDADNPCS = 10;

  /** The Constant TIPONPC. */
  private static final String TIPONPC = "npc";
  
  /**Posiciones en x de los npcs*/
  private static final int[] posXnpc = {544,160,672,416,160,-192,-224,-576,-32,832};
  
  /**Posiciones en Y de los npcs*/
  private static final int[] posYnpc = {304,496,720,880,752,672,464,544,1072,1024};
  
  /**
   * Instantiates a new escucha cliente.
   *
   * @param ipAux the ip
   * @param socketAux the socket
   * @param entradaAux the entrada
   * @param salidaAux the salida
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public EscuchaCliente(final String ipAux, final Socket socketAux,
      final ObjectInputStream entradaAux, final ObjectOutputStream salidaAux)
      throws IOException {
    this.socket = socketAux;
    this.entrada = entradaAux;
    this.salida = salidaAux;
    paquetePersonaje = new PaquetePersonaje();
    paqueteNpc = new PaqueteNPC();
  }

  /* (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
public void run() {
    try {
      ComandosServer comand;
      Paquete paquete;
      Paquete paqueteSv = new Paquete(null, 0);
      paqueteUsuario = new PaqueteUsuario();
      String cadenaLeida = (String) entrada.readObject();

      while (!((paquete = gson.fromJson(cadenaLeida, Paquete.class))
      .getComando() == Comando.DESCONECTAR)) {
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
        paqueteDePersonajes = new PaqueteDePersonajes(
            Servidor.getPersonajesConectados());
        paqueteDePersonajes.setComando(Comando.CONEXION);
        conectado.salida.writeObject(gson
            .toJson(paqueteDePersonajes, PaqueteDePersonajes.class));
      }

      Servidor.getLog().append(paquete.getIp()
          + " se ha desconectado." + System.lineSeparator());

    } catch (IOException | ClassNotFoundException
        | InstantiationException | IllegalAccessException e) {
      Servidor.getLog().append("Error de conexion: "
          + e.getMessage() + System.lineSeparator());
    }
  }

  /**
   * Gets the socket.
   *
   * @return the socket
   */
  public Socket getSocket() {
    return socket;
  }

  /**
   * Gets the entrada.
   *
   * @return the entrada
   */
  public ObjectInputStream getEntrada() {
    return entrada;
  }

  /**
   * Gets the salida.
   *
   * @return the salida
   */
  public ObjectOutputStream getSalida() {
    return salida;
  }

  /**
   * Gets the paquete personaje.
   *
   * @return the paquete personaje
   */
  public PaquetePersonaje getPaquetePersonaje() {
    return paquetePersonaje;
  }

  /**
   * Gets the id personaje.
   *
   * @return the id personaje
   */
  public int getIdPersonaje() {
    return idPersonaje;
  }

  /**
   * Gets the paquete movimiento.
   *
   * @return the paquete movimiento
   */
  public PaqueteMovimiento getPaqueteMovimiento() {
    return paqueteMovimiento;
  }

  /**
   * Sets the paquete movimiento.
   *
   * @param paqueteMovimientoAux the new paquete movimiento
   */
  public void setPaqueteMovimiento(
  final PaqueteMovimiento paqueteMovimientoAux) {
    this.paqueteMovimiento = paqueteMovimientoAux;
  }

  /**
   * Gets the paquete batalla.
   *
   * @return the paquete batalla
   */
  public PaqueteBatalla getPaqueteBatalla() {
    return paqueteBatalla;
  }

  /**
   * Sets the paquete batalla.
   *
   * @param paqueteBatallaAux the new paquete batalla
   */
  public void setPaqueteBatalla(
  final PaqueteBatalla paqueteBatallaAux) {
    this.paqueteBatalla = paqueteBatallaAux;
  }

  /**
   * Gets the paquete atacar.
   *
   * @return the paquete atacar
   */
  public PaqueteAtacar getPaqueteAtacar() {
    return paqueteAtacar;
  }

  /**
   * Sets the paquete atacar.
   *
   * @param paqueteAtacarAux the new paquete atacar
   */
  public void setPaqueteAtacar(
  final PaqueteAtacar paqueteAtacarAux) {
    this.paqueteAtacar = paqueteAtacarAux;
  }

  /**
   * Gets the paquete finalizar batalla.
   *
   * @return the paquete finalizar batalla
   */
  public PaqueteFinalizarBatalla getPaqueteFinalizarBatalla() {
    return paqueteFinalizarBatalla;
  }

  /**
   * Sets the paquete finalizar batalla.
   *
   * @param paqueteFinalizarBatallaAux the new paquete finalizar batalla
   */
  public void setPaqueteFinalizarBatalla(
  final PaqueteFinalizarBatalla paqueteFinalizarBatallaAux) {
    this.paqueteFinalizarBatalla = paqueteFinalizarBatallaAux;
  }

  /**
   * Gets the paquete de movimiento.
   *
   * @return the paquete de movimiento
   */
  public PaqueteDeMovimientos getPaqueteDeMovimiento() {
    return paqueteDeMovimiento;
  }

  /**
   * Sets the paquete de movimiento.
   *
   * @param paqueteDeMovimientoAux the new paquete de movimiento
   */
  public void setPaqueteDeMovimiento(
  final PaqueteDeMovimientos paqueteDeMovimientoAux) {
    this.paqueteDeMovimiento = paqueteDeMovimientoAux;
  }

  /**
   * Gets the paquete de personajes.
   *
   * @return the paquete de personajes
   */
  public PaqueteDePersonajes getPaqueteDePersonajes() {
    return paqueteDePersonajes;
  }

  /**
   * Sets the paquete de personajes.
   *
   * @param paqueteDePersonajesAux the new paquete de personajes
   */
  public void setPaqueteDePersonajes(
  final PaqueteDePersonajes paqueteDePersonajesAux) {
    this.paqueteDePersonajes = paqueteDePersonajesAux;
  }

  /**
   * Sets the id personaje.
   *
   * @param idPersonajeAux the new id personaje
   */
  public void setIdPersonaje(final int idPersonajeAux) {
    this.idPersonaje = idPersonajeAux;
  }

  /**
   * Sets the paquete personaje.
   *
   * @param paquetePersonajeAux the new paquete personaje
   */
  public void setPaquetePersonaje(final PaquetePersonaje paquetePersonajeAux) {
    this.paquetePersonaje = paquetePersonajeAux;
  }

  /**
   * Gets the paquete usuario.
   *
   * @return the paquete usuario
   */
  public PaqueteUsuario getPaqueteUsuario() {
    return paqueteUsuario;
  }

  /**
   * Sets the paquete usuario.
   *
   * @param paqueteUsuarioAux the new paquete usuario
   */
  public void setPaqueteUsuario(
  final PaqueteUsuario paqueteUsuarioAux) {
    this.paqueteUsuario = paqueteUsuarioAux;
  }

  /**
   * Inicializar NPCS.
   */
  public static void inicializarNPCS() {
    for (int i = 0; i < CANTIDADNPCS; i++) {
  	  if (i == 0) {
          Servidor.getNpcsActivos().put(i, new PaqueteNPC(i, "Npc"
              + i, TIPONPC, 1, 1, posXnpc[i],posYnpc[i]));
        } else if (i < 7) {
          Servidor.getNpcsActivos().put(i, new PaqueteNPC(i, "Npc" + i,
              TIPONPC, 1, i+1,posXnpc[i],posYnpc[i]));
        } else {
          Servidor.getNpcsActivos().put(i, new PaqueteNPC(i, "Npc" + i,
              TIPONPC, 1, 7, posXnpc[i],posYnpc[i]));
        }
    }
  }

  /**
   * Gets the paquete npc.
   *
   * @return the paquete npc
   */
  public PaqueteNPC getPaqueteNpc() {
    return paqueteNpc;
  }

  /**
   * Sets the paquete npc.
   *
   * @param paqueteNpcAux the new paquete npc
   */
  public void setPaqueteNpc(final PaqueteNPC paqueteNpcAux) {
    this.paqueteNpc = paqueteNpcAux;
  }

  /**
   * Enviar paquete NPC.
   */
  public void enviarPaqueteNPC() {
    paqueteDeNpcs = new PaqueteDeNPCS(Servidor.getNpcsActivos());
    paqueteDeNpcs.setComando(Comando.SETEARNPC);
    try {
      this.salida.writeObject(gson.toJson(paqueteDeNpcs, PaqueteDeNPCS.class));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

/**
 * Gets the paquete batalla NPC.
 *
 * @return the paquete batalla NPC
 */
public PaqueteBatallaNPC getPaqueteBatallaNPC() {
    return paqueteBatallaNPC;
}

/**
 * Sets the paquete batalla NPC.
 *
 * @param paqueteBatallaNPCAux the new paquete batalla NPC
 */
public void setPaqueteBatallaNPC(final PaqueteBatallaNPC paqueteBatallaNPCAux) {
   this.paqueteBatallaNPC = paqueteBatallaNPCAux;
}

/**
 * Gets the paquete de npcs.
 *
 * @return the paquete de npcs
 */
public PaqueteDeNPCS getPaqueteDeNpcs() {
   return paqueteDeNpcs;
}

/**
 * Sets the paquete de npcs.
 *
 * @param paqueteDeNpcsAux the new paquete de npcs
 */
public void setPaqueteDeNpcs(final PaqueteDeNPCS paqueteDeNpcsAux) {
   this.paqueteDeNpcs = paqueteDeNpcsAux;
}

}
