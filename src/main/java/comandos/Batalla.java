package comandos;

import estados.Estado;

import java.io.IOException;

import mensajeria.PaqueteBatalla;
import servidor.EscuchaCliente;
import servidor.Servidor;


/**
 * The Class Batalla.
 */
public class Batalla extends ComandosServer {

  /* (non-Javadoc)
   * @see mensajeria.Comando#ejecutar()
   */
  @Override
public void ejecutar() {
    // Le reenvio al id del personaje batallado que quieren pelear
    getEscuchaCliente().setPaqueteBatalla((PaqueteBatalla)
        gson.fromJson(cadenaLeida, PaqueteBatalla.class));

    Servidor.getLog().append(getEscuchaCliente().getPaqueteBatalla().getId()
        + " quiere batallar con " + getEscuchaCliente().getPaqueteBatalla()
        .getIdEnemigo() + System.lineSeparator());
    try {

      // seteo estado de batalla
      Servidor.getPersonajesConectados().get(getEscuchaCliente()
          .getPaqueteBatalla().getId()).setEstado(Estado.estadoBatalla);
      Servidor.getPersonajesConectados().get(getEscuchaCliente()
          .getPaqueteBatalla().getIdEnemigo()).setEstado(Estado.estadoBatalla);
      getEscuchaCliente().getPaqueteBatalla().setMiTurno(true);
      getEscuchaCliente().getSalida()
          .writeObject(gson.toJson(getEscuchaCliente().getPaqueteBatalla()));

      for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
        if (conectado.getIdPersonaje() == getEscuchaCliente()
        .getPaqueteBatalla().getIdEnemigo()) {
          int aux = getEscuchaCliente().getPaqueteBatalla().getId();
          getEscuchaCliente().getPaqueteBatalla()
              .setId(getEscuchaCliente().getPaqueteBatalla().getIdEnemigo());
          getEscuchaCliente().getPaqueteBatalla().setIdEnemigo(aux);
          getEscuchaCliente().getPaqueteBatalla().setMiTurno(false);
          conectado.getSalida().writeObject(gson
              .toJson(getEscuchaCliente().getPaqueteBatalla()));
          break;
        }
      }
    } catch (IOException e) {
      Servidor.getLog().append("Falló al intentar enviar Batalla \n");
    }

    synchronized (Servidor.getAtencionConexiones()) {
      Servidor.getAtencionConexiones().notify();
    }
  }
}
