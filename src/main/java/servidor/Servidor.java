package servidor;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import mensajeria.PaqueteMensaje;
import mensajeria.PaqueteMovimiento;
import mensajeria.PaqueteNPC;
import mensajeria.PaquetePersonaje;

/**
 * The Class Servidor.
 */
public class Servidor extends Thread {

  /** The clientes conectados. */
  private static ArrayList<EscuchaCliente>
      clientesConectados = new ArrayList<>();

  /** The ubicacion personajes. */
  private static Map<Integer, PaqueteMovimiento>
      ubicacionPersonajes = new HashMap<>();

  /** The personajes conectados. */
  private static Map<Integer, PaquetePersonaje>
      personajesConectados = new HashMap<>();

  /** The server. */
  private static Thread server;

  /** The server socket. */
  private static ServerSocket serverSocket;

  /** The conexion DB. */
  private static Conector conexionDB;

  /** The puerto. */
  private static final int PUERTO = 55050;

  /** The Constant ANCHO. */
  private static final  int ANCHO = 700;

  /** The Constant ALTO. */
  private static final  int ALTO = 640;

  /** The Constant ALTO_LOG. */
  private static final  int ALTO_LOG = 520;

  /** The Constant ANCHO_LOG. */
  private static final  int ANCHO_LOG = ANCHO - 25;

  /** The log. */
  private static JTextArea log;

  /** The atencion conexiones. */
  private static AtencionConexiones atencionConexiones;

  /** The atencion movimientos. */
  private static AtencionMovimientos atencionMovimientos;

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(final String[] args) {
    cargarInterfaz();
  }

  /** The npcs activos. */
  private static HashMap<Integer, PaqueteNPC> npcsActivos = new HashMap<>();

  /**
   * Cargar interfaz.
   */
  private static void cargarInterfaz() {
    JFrame ventana = new JFrame("Servidor WOME");
    ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ventana.setSize(ANCHO, ALTO);
    ventana.setResizable(false);
    ventana.setLocationRelativeTo(null);
    ventana.setLayout(null);
    ventana.setIconImage(Toolkit.getDefaultToolkit()
        .getImage("src/main/java/servidor/server.png"));
    JLabel titulo = new JLabel("Log del servidor...");
    titulo.setFont(new Font("Courier New", Font.BOLD, 16));
    titulo.setBounds(10, 0, 200, 30);
    ventana.add(titulo);

    setLog(new JTextArea());
    getLog().setEditable(false);
    getLog().setFont(new Font("Times New Roman", Font.PLAIN, 13));
    JScrollPane scroll =
        new JScrollPane(getLog(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scroll.setBounds(10, 40, ANCHO_LOG, ALTO_LOG);
    ventana.add(scroll);

    final JButton botonIniciar = new JButton();
    final JButton botonDetener = new JButton();
    botonIniciar.setText("Iniciar");
    botonIniciar.setBounds(220, ALTO - 70, 100, 30);
    botonIniciar.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent e) {
        server = new Thread(new Servidor());
        server.start();
        botonIniciar.setEnabled(false);
        botonDetener.setEnabled(true);
      }
      });
    ventana.add(botonIniciar);

    botonDetener.setText("Detener");
    botonDetener.setBounds(360, ALTO - 70, 100, 30);
    botonDetener.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent e) {
        try {
          server.stop();
          getAtencionConexiones().stop();
          getAtencionMovimientos().stop();
          for (EscuchaCliente cliente : clientesConectados) {
            cliente.getSalida().close();
            cliente.getEntrada().close();
            cliente.getSocket().close();
          }
          serverSocket.close();
          getLog()
              .append("El servidor se ha detenido." + System.lineSeparator());
          } catch (IOException e1) {
            getLog().append("Fallo al intentar detener el servidor."
                + System.lineSeparator());
          }
          if (conexionDB != null) {
            conexionDB.close();
          }
          botonDetener.setEnabled(false);
          botonIniciar.setEnabled(true);
          }
      });
    botonDetener.setEnabled(false);
    ventana.add(botonDetener);

    ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    ventana.addWindowListener(new WindowAdapter() {
      public void windowClosing(final WindowEvent evt) {
        if (serverSocket != null) {
          try {
            server.stop();
            getAtencionConexiones().stop();
            getAtencionMovimientos().stop();
            for (EscuchaCliente cliente : clientesConectados) {
              cliente.getSalida().close();
              cliente.getEntrada().close();
              cliente.getSocket().close();
            }
            serverSocket.close();
            getLog()
               .append("El servidor se ha detenido." + System.lineSeparator());
            } catch (IOException e) {
            getLog().append("Fallo al intentar detener el servidor."
                + System.lineSeparator());
            System.exit(1);
          }
        }
        if (conexionDB != null) {
          conexionDB.close();
        }
        System.exit(0);
        }
      });
    ventana.setVisible(true);
  }

  /**
   * Gets the npcs activos.
   *
   * @return the npcs activos
   */
  public static HashMap<Integer, PaqueteNPC> getNpcsActivos() {
    return npcsActivos;
  }

  /**
   * Sets the npcs activos.
   *
   * @param npcsActivosAux the npcs activos
   */
  public static void setNpcsActivos(
      final HashMap<Integer, PaqueteNPC> npcsActivosAux) {
    Servidor.npcsActivos = npcsActivosAux;
  }

  /* (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    try {
      conexionDB = new Conector();
      conexionDB.connect();
      getLog().append("Iniciando el servidor..." + System.lineSeparator());
      serverSocket = new ServerSocket(PUERTO);
      getLog()
          .append("Servidor esperando conexiones..." + System.lineSeparator());
      String ipRemota;
      setAtencionConexiones(new AtencionConexiones());
      setAtencionMovimientos(new AtencionMovimientos());

      getAtencionConexiones().start();
      getAtencionMovimientos().start();
      EscuchaCliente.inicializarNPCS();

      while (true) {
        Socket cliente = serverSocket.accept();
        ipRemota = cliente.getInetAddress().getHostAddress();
        getLog().append(ipRemota + " se ha conectado" + System.lineSeparator());
        ObjectOutputStream salida = new ObjectOutputStream(
            cliente.getOutputStream());
        ObjectInputStream entrada = new ObjectInputStream(
            cliente.getInputStream());
        EscuchaCliente atencion = new EscuchaCliente(
            ipRemota, cliente, entrada, salida);
        atencion.start();
        clientesConectados.add(atencion);
      }
    } catch (Exception e) {
      getLog().append("Fallo la conexión." + System.lineSeparator());
    }
  }

  /**
   * Mensaje A usuario.
   *
   * @param pqm the pqm
   * @return true, if successful
   */
  public static boolean mensajeAUsuario(final PaqueteMensaje pqm) {
    boolean result = true;
    boolean noEncontro = true;
    for (Map.Entry<Integer,
    PaquetePersonaje> personaje : personajesConectados.entrySet()) {
      if (noEncontro && (!personaje.getValue()
      .getNombre().equals(pqm.getUserReceptor()))) {
        result = false;
      } else {
        result = true;
        noEncontro = false;
      }
    }
    // Si existe inicio sesion
    if (result) {
      Servidor.getLog().append(pqm.getUserEmisor()
          + " envió mensaje a " + pqm.getUserReceptor()
          + System.lineSeparator());
      return true;
    } else {
      // Si no existe informo y devuelvo false
      Servidor.getLog().append("El mensaje para " + pqm.getUserReceptor()
          + " no se envió, ya que se encuentra desconectado."
          + System.lineSeparator());
      return false;
    }
  }

  /**
   * Mensaje A all.
   *
   * @param contador the contador
   * @return true, if successful
   */
  public static boolean mensajeAAll(final int contador) {
    boolean result = true;
    if (personajesConectados.size() != contador + 1) {
      result = false;
    }
    // Si existe inicio sesion
    if (result) {
      Servidor.getLog().append("Se ha enviado un mensaje a todos los usuarios"
      + System.lineSeparator());
      return true;
    } else {
      // Si no existe informo y devuelvo false
      Servidor.getLog()
          .append("Uno o más de todos los usuarios se ha desconectado,"
          + " se ha mandado el mensaje a los demas." + System.lineSeparator());
      return false;
    }
  }

  /**
   * Gets the clientes conectados.
   *
   * @return the clientes conectados
   */
  public static ArrayList<EscuchaCliente> getClientesConectados() {
    return clientesConectados;
  }

  /**
   * Gets the ubicacion personajes.
   *
   * @return the ubicacion personajes
   */
  public static Map<Integer, PaqueteMovimiento> getUbicacionPersonajes() {
    return ubicacionPersonajes;
  }

  /**
   * Gets the personajes conectados.
   *
   * @return the personajes conectados
   */
  public static Map<Integer, PaquetePersonaje> getPersonajesConectados() {
    return personajesConectados;
  }

  /**
   * Gets the conector.
   *
   * @return the conector
   */
  public static Conector getConector() {
    return conexionDB;
  }

  /**
   * Gets the JTextArea.
   *
   * @return the JTextArea
   */
  public static JTextArea getLog() {
    return log;
  }

  /**
   * Sets the log.
   *
   * @param logAux the new log
   */
  public static void setLog(final JTextArea logAux) {
    Servidor.log = logAux;
  }

  /**
   * Gets the AtencionConexiones.
   *
   * @return the AtencionConexiones
   */

  public static AtencionConexiones getAtencionConexiones() {
    return atencionConexiones;
  }

  /**
   * Sets the atencion conexiones.
   *
   * @param atencionConexionesAux the new atencion conexiones
   */
  public static void setAtencionConexiones(
  final AtencionConexiones atencionConexionesAux) {
    Servidor.atencionConexiones = atencionConexionesAux;
  }

  /**
   * Gets the AtencionMovimientos.
   *
   * @return the AtencionMovimientos.
   */

  public static AtencionMovimientos getAtencionMovimientos() {
    return atencionMovimientos;
  }

  /**
   * Sets the atencion movimientos.
   *
   * @param atencionMovimientosAux the new atencion movimientos
   */
  public static void setAtencionMovimientos(
 final AtencionMovimientos atencionMovimientosAux) {
    Servidor.atencionMovimientos = atencionMovimientosAux;
}
}
