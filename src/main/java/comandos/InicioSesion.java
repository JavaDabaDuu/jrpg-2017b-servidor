package comandos;

import java.io.IOException;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
import servidor.Servidor;

/**
 * The Class InicioSesion.
 */
public class InicioSesion extends ComandosServer {

  /* (non-Javadoc)
   * @see mensajeria.Comando#ejecutar()
   */
  @Override
public void ejecutar() {
    Paquete paqueteSv = new Paquete(null, 0);
    paqueteSv.setComando(Comando.INICIOSESION);
    // Recibo el paquete usuario
    getEscuchaCliente().setPaqueteUsuario((PaqueteUsuario)
        (gson.fromJson(cadenaLeida, PaqueteUsuario.class)));
    // Si se puede loguear el usuario le envio un mensaje de exito
    //y el paquete personaje con los datos
    try {
      if (Servidor.getConector()
      .loguearUsuario(getEscuchaCliente().getPaqueteUsuario())) {
        PaquetePersonaje paquetePersonaje = new PaquetePersonaje();
        paquetePersonaje = Servidor.getConector()
            .getPersonaje(getEscuchaCliente().getPaqueteUsuario());
        paquetePersonaje.setComando(Comando.INICIOSESION);
        paquetePersonaje.setMensaje(Paquete.msjExito);
        getEscuchaCliente().setIdPersonaje(paquetePersonaje.getId());
        getEscuchaCliente().getSalida()
            .writeObject(gson.toJson(paquetePersonaje));

      } else {
        paqueteSv.setMensaje(Paquete.msjFracaso);
        getEscuchaCliente().getSalida().writeObject(gson.toJson(paqueteSv));
      }
    } catch (IOException e) {
      Servidor.getLog().append("Falló al intentar iniciar sesión \n");
    }
  }
}
