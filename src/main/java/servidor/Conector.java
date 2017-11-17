
package servidor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import hibernate.Inventario;
import hibernate.Mochila;

/**
 * The Class Conector.
 */
public class Conector {

	/** The url. */
	private String url = "primeraBase.bd";

	/** The connect. */
	private Connection connect;

	/** The session factory. */

	private SessionFactory factory;

	public SessionFactory getSessionFactory() {
		return this.factory;
	}

	public void setSessionFactory(SessionFactory factory) {
		this.factory = factory;
	}

	/**
	 * Connect.
	 */
	public void connect() {
		try {
			Servidor.getLog().append("Estableciendo conexión con la base de datos..." + System.lineSeparator());
			connect = DriverManager.getConnection("jdbc:sqlite:" + url);
			Servidor.getLog().append("Conexión con la base de datos establecida con éxito." + System.lineSeparator());

			try {
				// configuramos hibernate segun nuestro xml de configuracion
				Configuration cfg = new Configuration();
				cfg.configure("hibernate.cfg.xml");
				Servidor.getLog().append("Inicializando SessionFactory..." + System.lineSeparator());
				this.setSessionFactory(cfg.buildSessionFactory());
			} catch (HibernateException he) {
				Servidor.getLog().append("Ocurrió un error en la inicialización de la SessionFactory: " + he);
				throw new ExceptionInInitializerError(he);
			}
		} catch (SQLException ex) {
			Servidor.getLog().append("Fallo al intentar establecer la conexión con la base de datos. " + ex.getMessage()
					+ System.lineSeparator());
		}
	}

	/**
	 * Close.
	 */
	public void close() {
		try {
			connect.close();
		} catch (SQLException ex) {
			Servidor.getLog()
					.append("Error al intentar cerrar la conexión con la base de datos." + System.lineSeparator());
			Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Registrar usuario.
	 *
	 * @param user
	 *            the user
	 * @return true, if successful
	 */
	public boolean registrarUsuario(final PaqueteUsuario user) {

		// abrimos session
		Session session = getSessionFactory().openSession();

		// generamos consulta
		CriteriaBuilder cBuilder = session.getCriteriaBuilder();
		CriteriaQuery<PaqueteUsuario> cQuery = cBuilder.createQuery(PaqueteUsuario.class);
		Root<PaqueteUsuario> root = cQuery.from(PaqueteUsuario.class);

		cQuery.select(root).where(cBuilder.equal(root.get("username"), user.getUsername()));

		if (session.createQuery(cQuery).getResultList().isEmpty()) {

			Transaction transaccion = session.beginTransaction();
			try {
				session.save(user);
				transaccion.commit();

			} catch (HibernateException e) {
				if (transaccion != null)
					transaccion.rollback();
				e.printStackTrace();
				Servidor.getLog().append("Ocurrió un error al Registrar Usuario " + e);
				throw new ExceptionInInitializerError(e);
			}
		} else {
			session.close();
			Servidor.getLog()
					.append("El usuario " + user.getUsername() + " ya se encuentra en uso." + System.lineSeparator());
			return false;
		}

		session.close();
		Servidor.getLog().append("El usuario " + user.getUsername() + " se ha registrado." + System.lineSeparator());
		return true;
	}

	/**
	 * Registrar personaje.
	 *
	 * @param paquetePersonaje
	 *            the paquete personaje
	 * @param paqueteUsuario
	 *            the paquete usuario
	 * @return true, if successful
	 */
	public boolean registrarPersonaje(final PaquetePersonaje paquetePersonaje, final PaqueteUsuario paqueteUsuario) {

		Session session = getSessionFactory().openSession();

		Transaction transaccion = session.beginTransaction();
		try {

			// grabo personaje sin inventario y sin mochila
			session.save(paquetePersonaje);
			Servidor.getLog().append(paqueteUsuario.getUsername() + " ha creado el personaje: "
					+ paquetePersonaje.getId() + System.lineSeparator());

			paqueteUsuario.setIdPj(paquetePersonaje.getId());
			session.update(paqueteUsuario);

			// Registramos Inventario
			final Inventario inventario = new Inventario(paquetePersonaje.getId());
			session.save(inventario);

			// Registramos Mochila
			final Mochila m = new Mochila(paquetePersonaje.getId());
			session.save(m);

			// le seteo la nueva mochila e inventario recien creados
			paquetePersonaje.setIdInventario(inventario.getidInventario());
			paquetePersonaje.setIdMochila(m.getIdMochila());
			session.update(paquetePersonaje);
			session.update(m);
			transaccion.commit();

			Servidor.getLog().append("se ha creado el personaje " + paquetePersonaje.getId() + System.lineSeparator());

			session.close();

			return true;

		} catch (HibernateException e) {
			if (transaccion != null)
				transaccion.rollback();
			e.printStackTrace();

			session.close();
			Servidor.getLog().append(
					"Error al intentar crear el personaje " + paquetePersonaje.getNombre() + System.lineSeparator());
			return false;
		}

	}

	/**
	 * Loguear usuario.
	 *
	 * @param user
	 *            the user
	 * @return true, if successful
	 */

	@Transactional
	public boolean loguearUsuario(final PaqueteUsuario user) {
		String pwConsulta;
		String pwPaqueteUsuario;
		Session session = getSessionFactory().openSession();

		try {

			CriteriaBuilder cBuilder = session.getCriteriaBuilder();
			CriteriaQuery<PaqueteUsuario> cQuery = cBuilder.createQuery(PaqueteUsuario.class);
			Root<PaqueteUsuario> root = cQuery.from(PaqueteUsuario.class);

			// busco usuario con el nombre ingresado
			cQuery.select(root).where(cBuilder.equal(root.get("username"), user.getUsername()));

			pwConsulta = session.createQuery(cQuery).getResultList().remove(0).getPassword();
			pwPaqueteUsuario = user.getPassword();

			// pregunto si lo encontro
			if (!session.createQuery(cQuery).getResultList().isEmpty() && pwConsulta.equals(pwPaqueteUsuario)) {

				Servidor.getLog()
						.append("El usuario " + user.getUsername() + " ha iniciado sesión." + System.lineSeparator());

				return true;
			}
			return false;

		} catch (HibernateException e) {
			Servidor.getLog().append("Error al loguear usuario " + user.getUsername() + System.lineSeparator());
			Servidor.getLog().append(e.getMessage() + System.lineSeparator());
			return false;
		} finally {

			session.close();

		}
	}

	/**
	 * Actualizar personaje.
	 *
	 * @param paquetePersonaje
	 *            the paquete personaje
	 */
	public void actualizarPersonaje(final PaquetePersonaje paquetePersonaje) {
		try {
			int i = 2;
			PreparedStatement stActualizarPersonaje = connect
					.prepareStatement("UPDATE personaje SET fuerza=?, destreza=?"
							+ ", inteligencia=?, saludTope=?, energiaTope=?, experiencia=?,"
							+ " nivel=?, puntosNivel=?  WHERE idPersonaje=?");
			stActualizarPersonaje.setInt(1, paquetePersonaje.getFuerza());
			stActualizarPersonaje.setInt(2, paquetePersonaje.getDestreza());
			stActualizarPersonaje.setInt(3, paquetePersonaje.getInteligencia());
			stActualizarPersonaje.setInt(4, paquetePersonaje.getSaludTope());
			stActualizarPersonaje.setInt(5, paquetePersonaje.getEnergiaTope());
			stActualizarPersonaje.setInt(6, paquetePersonaje.getExperiencia());
			stActualizarPersonaje.setInt(7, paquetePersonaje.getNivel());
			stActualizarPersonaje.setInt(8, paquetePersonaje.getPuntosNivel());
			// Se agrega puntos por nivel a actualizar
			stActualizarPersonaje.setInt(9, paquetePersonaje.getId());
			stActualizarPersonaje.executeUpdate();
			PreparedStatement stDameItemsID = connect.prepareStatement("SELECT * FROM mochila WHERE idMochila = ?");
			stDameItemsID.setInt(1, paquetePersonaje.getId());
			ResultSet resultadoItemsID = stDameItemsID.executeQuery();
			PreparedStatement stDatosItem = connect.prepareStatement("SELECT * FROM item WHERE idItem = ?");
			ResultSet resultadoDatoItem = null;
			paquetePersonaje.eliminarItems();
			int j = 1;
			while (j <= 9) {
				if (resultadoItemsID.getInt(i) != -1) {
					stDatosItem.setInt(1, resultadoItemsID.getInt(i));
					resultadoDatoItem = stDatosItem.executeQuery();
					paquetePersonaje.anadirItem(resultadoDatoItem.getInt("idItem"),
							resultadoDatoItem.getString("nombre"), resultadoDatoItem.getInt("wereable"),
							resultadoDatoItem.getInt("bonusSalud"), resultadoDatoItem.getInt("bonusEnergia"),
							resultadoDatoItem.getInt("bonusFuerza"), resultadoDatoItem.getInt("bonusDestreza"),
							resultadoDatoItem.getInt("bonusInteligencia"), resultadoDatoItem.getString("foto"),
							resultadoDatoItem.getString("fotoEquipado"));
				}
				i++;
				j++;
			}
			Servidor.getLog().append("El personaje " + paquetePersonaje.getNombre() + " se ha actualizado con éxito."
					+ System.lineSeparator());
			;
		} catch (SQLException e) {
			Servidor.getLog().append("Fallo al intentar actualizar el personaje " + paquetePersonaje.getNombre()
					+ System.lineSeparator());
		}
	}

	/**
	 * Gets the personaje.
	 *
	 * @param user
	 *            the user
	 * @return the personaje
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public PaquetePersonaje getPersonaje(final PaqueteUsuario user) throws IOException {
		ResultSet result = null;
		ResultSet resultadoItemsID = null;
		ResultSet resultadoDatoItem = null;
		int i = 2;
		int j = 0;
		try {
			// Selecciono el personaje de ese usuario
			PreparedStatement st = connect.prepareStatement("SELECT * FROM registro WHERE usuario = ?");
			st.setString(1, user.getUsername());
			result = st.executeQuery();

			// Obtengo el id
			int idPersonaje = result.getInt("idPersonaje");

			// Selecciono los datos del personaje
			PreparedStatement stSeleccionarPersonaje = connect
					.prepareStatement("SELECT * FROM personaje WHERE idPersonaje = ?");
			stSeleccionarPersonaje.setInt(1, idPersonaje);
			result = stSeleccionarPersonaje.executeQuery();
			// Traigo los id de los items correspondientes a mi personaje
			PreparedStatement stDameItemsID = connect.prepareStatement("SELECT * FROM mochila WHERE idMochila = ?");
			stDameItemsID.setInt(1, idPersonaje);
			resultadoItemsID = stDameItemsID.executeQuery();
			// Traigo los datos del item
			PreparedStatement stDatosItem = connect.prepareStatement("SELECT * FROM item WHERE idItem = ?");

			// Obtengo los atributos del personaje
			PaquetePersonaje personaje = new PaquetePersonaje();
			personaje.setId(idPersonaje);
			personaje.setRaza(result.getString("raza"));
			personaje.setCasta(result.getString("casta"));
			personaje.setFuerza(result.getInt("fuerza"));
			personaje.setInteligencia(result.getInt("inteligencia"));
			personaje.setDestreza(result.getInt("destreza"));
			personaje.setEnergiaTope(result.getInt("energiaTope"));
			personaje.setSaludTope(result.getInt("saludTope"));
			personaje.setNombre(result.getString("nombre"));
			personaje.setExperiencia(result.getInt("experiencia"));
			personaje.setNivel(result.getInt("nivel"));
			personaje.setPuntosNivel(result.getInt("puntosNivel"));

			while (j <= 9) {
				if (resultadoItemsID.getInt(i) != -1) {
					stDatosItem.setInt(1, resultadoItemsID.getInt(i));
					resultadoDatoItem = stDatosItem.executeQuery();
					personaje.anadirItem(resultadoDatoItem.getInt("idItem"), resultadoDatoItem.getString("nombre"),
							resultadoDatoItem.getInt("wereable"), resultadoDatoItem.getInt("bonusSalud"),
							resultadoDatoItem.getInt("bonusEnergia"), resultadoDatoItem.getInt("bonusFuerza"),
							resultadoDatoItem.getInt("bonusDestreza"), resultadoDatoItem.getInt("bonusInteligencia"),
							resultadoDatoItem.getString("foto"), resultadoDatoItem.getString("fotoEquipado"));
				}
				i++;
				j++;
			}
			// Devuelvo el paquete personaje con sus datos
			return personaje;
		} catch (SQLException ex) {
			Servidor.getLog()
					.append("Fallo al intentar recuperar el personaje " + user.getUsername() + System.lineSeparator());
			Servidor.getLog().append(ex.getMessage() + System.lineSeparator());
		}
		return new PaquetePersonaje();
	}

	/**
	 * Gets the usuario.
	 *
	 * @param usuario
	 *            the usuario
	 * @return the usuario
	 */
	public PaqueteUsuario getUsuario(final String usuario) {
		String password;
		int idPersonaje;
		Session session = getSessionFactory().openSession();

		try {
			CriteriaBuilder cBuilder = session.getCriteriaBuilder();
			CriteriaQuery<PaqueteUsuario> cQuery = cBuilder.createQuery(PaqueteUsuario.class);
			Root<PaqueteUsuario> root = cQuery.from(PaqueteUsuario.class);

			// busco usuario con el nombre ingresado
			cQuery.select(root).where(cBuilder.equal(root.get("username"), usuario));

			// guardo los atributos que me trajo el select
			password = session.createQuery(cQuery).getResultList().remove(0).getPassword();
			idPersonaje = session.createQuery(cQuery).getResultList().remove(0).getIdPj();

			PaqueteUsuario paqueteUsuario = new PaqueteUsuario();

			paqueteUsuario.setUsername(usuario);
			paqueteUsuario.setPassword(password);
			paqueteUsuario.setIdPj(idPersonaje);
			return paqueteUsuario;

		} catch (HibernateException e) {
			Servidor.getLog().append("Fallo al intentar recuperar el usuario " + usuario + System.lineSeparator());
			Servidor.getLog().append(e.getMessage() + System.lineSeparator());
		}
		return new PaqueteUsuario();
	}

	/**
	 * Actualizar inventario.
	 *
	 * @param paquetePersonaje
	 *            the paquete personaje
	 */
	public void actualizarInventario(final PaquetePersonaje paquetePersonaje) {
		int i = 0;
		PreparedStatement stActualizarMochila;
		try {
			stActualizarMochila = connect
					.prepareStatement("UPDATE mochila SET item1=? ,item2=? ,item3=? ,item4=? ,item5=? "
							+ ",item6=? ,item7=?,item8=?, item9=?,item10=? ,item11=? ,item12=? "
							+ ",item13=? ,item14=? ,item15=? ,item16=? ,item17=? ,item18=? "
							+ ",item19=? ,item20=? WHERE idMochila=?");
			while (i < paquetePersonaje.getCantItems()) {
				stActualizarMochila.setInt(i + 1, paquetePersonaje.getItemID(i));
				i++;
			}
			for (int j = paquetePersonaje.getCantItems(); j < 20; j++) {
				stActualizarMochila.setInt(j + 1, -1);
			}
			stActualizarMochila.setInt(21, paquetePersonaje.getId());
			stActualizarMochila.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Actualizar inventario.
	 *
	 * @param idPersonaje
	 *            the id personaje
	 */
	public void actualizarInventario(final int idPersonaje) {
		int i = 0;
		PaquetePersonaje paquetePersonaje = Servidor.getPersonajesConectados().get(idPersonaje);
		PreparedStatement stActualizarMochila;
		try {
			stActualizarMochila = connect
					.prepareStatement("UPDATE mochila SET item1=? ,item2=? ,item3=? ,item4=? ,item5=? ,"
							+ "item6=? ,item7=? ,item8=? ,item9=?, item10=? ,item11=? ,item12=? "
							+ ",item13=? ,item14=? ,item15=? ,item16=? ,item17=? ,item18=? "
							+ ",item19=? ,item20=? WHERE idMochila=?");
			while (i < paquetePersonaje.getCantItems()) {
				stActualizarMochila.setInt(i + 1, paquetePersonaje.getItemID(i));
				i++;
			}
			if (paquetePersonaje.getCantItems() < 9) {
				int itemGanado = new Random().nextInt(29);
				itemGanado += 1;
				stActualizarMochila.setInt(paquetePersonaje.getCantItems() + 1, itemGanado);
				for (int j = paquetePersonaje.getCantItems() + 2; j < 20; j++) {
					stActualizarMochila.setInt(j, -1);
				}
			} else {
				for (int j = paquetePersonaje.getCantItems() + 1; j < 20; j++) {
					stActualizarMochila.setInt(j, -1);
				}
			}
			stActualizarMochila.setInt(21, paquetePersonaje.getId());
			stActualizarMochila.executeUpdate();
		} catch (SQLException e) {
			Servidor.getLog().append("Falló al intentar actualizar inventario de" + idPersonaje + "\n");
		}
	}

	/**
	 * Actualizar personaje subio nivel.
	 *
	 * @param paquetePersonaje
	 *            the paquete personaje
	 */
	public void actualizarPersonajeSubioNivel(final PaquetePersonaje paquetePersonaje) {

		Session session = getSessionFactory().openSession();

		Transaction transaccion = session.beginTransaction();
		try {

			// Actualizo Personaje
			session.update(paquetePersonaje);

			transaccion.commit();

			Servidor.getLog()
					.append("se ha Actualizado el Personaje" + paquetePersonaje.getId() + System.lineSeparator());

			session.close();

		} catch (HibernateException e) {
			if (transaccion != null)
				transaccion.rollback();
			e.printStackTrace();

			session.close();
			Servidor.getLog().append(
					"Error al intentar crear el personaje " + paquetePersonaje.getNombre() + System.lineSeparator());
		}

	}
}
