
package testsServidor;

import java.io.IOException;
import java.util.Random;

import javax.swing.plaf.SliderUI;

import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;

import org.junit.Assert;
import org.junit.Test;

import servidor.Conector;
import servidor.Servidor;

/**
 * The Class TestConector.
 */
public class TestConector {

  /**
   * Gets the random string.
   *
   * @return the random string
   */
  private String getRandomString() {
    String sALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < 8) { // length of the random string.
      int index = (int) (rnd.nextFloat() * sALTCHARS.length());
      salt.append(sALTCHARS.charAt(index));
    }
    String saltStr = salt.toString();
    return saltStr;
  }

  /**
   * Test conexion con la DB.
   */
  @Test
  public void testConexionConLaDB() {
    new Servidor();
    Servidor.main(null);
    Conector conector = new Conector();
    conector.connect();

    // Pasado este punto la conexi�n con la base de datos result� exitosa
    Assert.assertEquals(1, 1);
    conector.close();
  }

  /**
   * Test registrar usuario.
   */
  @Test
public void testRegistrarUsuario() {
    new Servidor();
    Servidor.main(null);
    Conector conector = new Conector();
    conector.connect();
    PaqueteUsuario pu = new PaqueteUsuario();
    pu.setUsername("UserTest");
    pu.setPassword("test");
    conector.registrarUsuario(pu);
    pu = conector.getUsuario("UserTest");
    Assert.assertEquals("UserTest", pu.getUsername());
    conector.close();
  }

  /**
   * Test registrar personaje.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
public void testRegistrarPersonaje() throws IOException {
    new Servidor();
    Servidor.main(null);

    Conector conector = new Conector();
    conector.connect();
    String randomero = getRandomString();
    PaquetePersonaje pp = new PaquetePersonaje();
    pp.setCasta("Humano");
    pp.setDestreza(1);
    pp.setEnergiaTope(1);
    pp.setExperiencia(1);
    pp.setFuerza(1);
    pp.setInteligencia(1);
    pp.setNivel(1);
    pp.setNombre(randomero);
    pp.setRaza("Asesino");
    pp.setSaludTope(1);

    PaqueteUsuario pu = new PaqueteUsuario();
    pu.setUsername(getRandomString());
    pu.setPassword("test");
    conector.registrarUsuario(pu);
    conector.registrarPersonaje(pp, pu);
    pp = conector.getPersonaje(pu);

    Assert.assertEquals(randomero, pp.getNombre());
    conector.close();
  }

  /**
   * Test login usuario.
   */
  @Test
public void testLoginUsuario() {
    new Servidor();
    Servidor.main(null);

    Conector conector = new Conector();
    conector.connect();

    PaqueteUsuario pu = new PaqueteUsuario();
    pu.setUsername("UserTest");
    pu.setPassword("test");

    conector.registrarUsuario(pu);

    boolean resultadoLogin = conector.loguearUsuario(pu);

    Assert.assertEquals(true, resultadoLogin);

    conector.close();
  }

  /**
   * Test login usuario fallido.
   */
  @Test
public void testLoginUsuarioFallido() {
    new Servidor();
    Servidor.main(null);

    Conector conector = new Conector();
    conector.connect();

    PaqueteUsuario pu = new PaqueteUsuario();
    pu.setUsername("userInventado");
    pu.setPassword("test");
    boolean resultadoLogin = conector.loguearUsuario(pu);
    Assert.assertEquals(false, resultadoLogin);
    conector.close();
  }
}
