package comandos;

import mensajeria.PaqueteMovimiento;
import servidor.Servidor;

/**
 * The Class Movimiento.
 */
public class Movimiento extends ComandosServer {

  /* (non-Javadoc)
   * @see mensajeria.Comando#ejecutar()
   */
  @Override
public void ejecutar() {
    getEscuchaCliente().setPaqueteMovimiento((PaqueteMovimiento)
        (gson.fromJson((String) cadenaLeida, PaqueteMovimiento.class)));
    Servidor.getUbicacionPersonajes()
        .get(getEscuchaCliente().getPaqueteMovimiento()
        .getIdPersonaje()).setPosX(getEscuchaCliente()
        .getPaqueteMovimiento().getPosX());
    Servidor.getUbicacionPersonajes()
        .get(getEscuchaCliente().getPaqueteMovimiento()
        .getIdPersonaje()).setPosY(getEscuchaCliente()
        .getPaqueteMovimiento().getPosY());
    Servidor.getUbicacionPersonajes()
        .get(getEscuchaCliente().getPaqueteMovimiento()
        .getIdPersonaje()).setDireccion(getEscuchaCliente()
        .getPaqueteMovimiento().getDireccion());
    Servidor.getUbicacionPersonajes()
        .get(getEscuchaCliente().getPaqueteMovimiento()
        .getIdPersonaje()).setFrame(getEscuchaCliente()
        .getPaqueteMovimiento().getFrame());
    synchronized (Servidor.getAtencionMovimientos()) {
      Servidor.getAtencionMovimientos().notify();
    }
  }
}
