
package comandos;

import java.io.IOException;

import mensajeria.PaquetePersonaje;
import servidor.Servidor;

/**
 * The Class CrearPersonaje.
 */
public class CrearPersonaje extends ComandosServer {

  /* (non-Javadoc)
   * @see mensajeria.Comando#ejecutar()
   */
  @Override
public void ejecutar() {
    // Casteo el paquete personaje
    getEscuchaCliente().setPaquetePersonaje((PaquetePersonaje)
        (getGson().fromJson(getCadenaLeida(), PaquetePersonaje.class)));
    // Guardo el personaje en ese usuario
    Servidor.getConector()
        .registrarPersonaje(getEscuchaCliente().getPaquetePersonaje(),
        getEscuchaCliente().getPaqueteUsuario());
    try {
      PaquetePersonaje paquetePersonaje;
      paquetePersonaje = new PaquetePersonaje();
      paquetePersonaje = Servidor.getConector()
          .getPersonaje(getEscuchaCliente().getPaqueteUsuario());
      getEscuchaCliente().setIdPersonaje(paquetePersonaje.getId());
      getEscuchaCliente().getSalida().writeObject(getGson()
          .toJson(getEscuchaCliente().getPaquetePersonaje(),
          getEscuchaCliente().getPaquetePersonaje().getClass()));
    } catch (IOException e1) {
      Servidor.getLog().append("Falló al intentar enviar personaje creado \n");
    }
  }

}
