package servidor;

import com.google.gson.Gson;

import estados.Estado;
import mensajeria.Comando;
import mensajeria.PaqueteDeMovimientos;

/**
 * The Class AtencionMovimientos.
 */
public class AtencionMovimientos extends Thread {

  /** The gson. */
  private final Gson gson = new Gson();

  /**
   * Instantiates a new atencion movimientos.
   */
  public AtencionMovimientos() {
  }

  /* (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
public void run() {

    synchronized (this) {
      try {
        while (true) {
          // Espero a que se conecte alguien
          wait();
          // Le reenvio la conexion a todos
          for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
            if (conectado.getPaquetePersonaje().getEstado()
            == Estado.getEstadoJuego()) {
              PaqueteDeMovimientos pdp = (PaqueteDeMovimientos)
                  new PaqueteDeMovimientos(Servidor
                  .getUbicacionPersonajes()).clone();
              pdp.setComando(Comando.MOVIMIENTO);
              synchronized (conectado) {
                conectado.getSalida().writeObject(gson.toJson(pdp));
              }
            }
          }
        }
      } catch (Exception e) {
        Servidor.getLog().append(
            "Falló al intentar enviar paqueteDeMovimientos \n");
      }
    }
  }
}
