
package servidor;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
import org.hibernate.query.Query;

import dominio.Inventario;
import dominio.Mochila;
import dominio.MyItem;
import hibernate.HibernateUtil;

/**
 * The Class Conector.
 */
public class Conector {

	private static final int CANTITEMS = 9;
	private static final int CANTITEMSMOCHILA = 20;

	/**
	 * Connect.
	 */
	public void connect() {
		try {

			Servidor.getLog().append("Estableciendo conexión con la base de datos..." + System.lineSeparator());
			HibernateUtil.buildSessionFactory();
			Servidor.getLog().append("Conexión con la base de datos establecida con éxito." + System.lineSeparator());

			
		} catch (HibernateException ex) {
			Servidor.getLog().append("Fallo al intentar establecer la conexión con la base de datos. " + ex.getMessage()
					+ System.lineSeparator());
		}
	}

	/**
	 * Close.
	 */
	public void close() {
		try {
			HibernateUtil.closeSessionAndUnbindFromThread();
		} catch (HibernateException ex) {
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

		try {
			// abrimos session
			HibernateUtil.openSessionAndBindToThread();

			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
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
				Servidor.getLog().append(
						"El usuario " + user.getUsername() + " ya se encuentra en uso." + System.lineSeparator());
				return false;
			}

			session.close();
			Servidor.getLog()
					.append("El usuario " + user.getUsername() + " se ha registrado." + System.lineSeparator());
			return true;

		} finally {
			HibernateUtil.closeSessionAndUnbindFromThread();
		}

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

		// Session session = getSessionFactory().openSession();

		HibernateUtil.openSessionAndBindToThread();

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
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

			return true;

		} catch (HibernateException e) {
			if (transaccion != null)
				transaccion.rollback();
			e.printStackTrace();

			Servidor.getLog().append(
					"Error al intentar crear el personaje " + paquetePersonaje.getNombre() + System.lineSeparator());
			return false;
		} finally {
			HibernateUtil.closeSessionAndUnbindFromThread();
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

		HibernateUtil.openSessionAndBindToThread();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

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
			HibernateUtil.closeSessionAndUnbindFromThread();
		}
	}

	/**
	 * Actualizar personaje.
	 *
	 * @param paquetePersonaje
	 *            the paquete personaje
	 */
	public void actualizarPersonaje(final PaquetePersonaje paquetePersonaje) {
		int i = 2;
		int j = 1;
		HibernateUtil.openSessionAndBindToThread();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			// actualizo nuevos atributos del Personaje
			session.update(paquetePersonaje);

			//query.executeUpdate();

			// me traigo la mochila del personaje con sus items
			Query queryMochila = session.createQuery("FROM Mochila WHERE idMochila = :idMochila");
			queryMochila.setParameter("idMochila", paquetePersonaje.getId());
			
			//pasos los items que tiene la mochila a la lista
			List<Mochila> resultadoItemsIDList = queryMochila.list();

			Query queryitem;

			Mochila resultadoItemsID;
			
			if (resultadoItemsIDList != null && !resultadoItemsIDList.isEmpty()) {
				
				//guardo mi primir item de la lsita
				resultadoItemsID = resultadoItemsIDList.get(0);
				
				//
				while (j <= CANTITEMS) {
					if (resultadoItemsID.getItemId(i) != -1) {
						
						//busco el item segun el id y se lo añado al persona
						queryitem = session.createQuery("FROM MyItem WHERE idItem = :idItem");
						queryitem.setParameter("idItem", resultadoItemsID.getItemId(i));

						List<MyItem> resultadoDatoItemList = queryitem.list();

						MyItem resultadoDatoItem = resultadoDatoItemList != null ? resultadoDatoItemList.get(0)
								: new MyItem();

						paquetePersonaje.anadirItem(resultadoDatoItem.getIdItem(), resultadoDatoItem.getNombre(),
								resultadoDatoItem.getWearLocation(), resultadoDatoItem.getBonusSalud(),
								resultadoDatoItem.getBonusEnergia(), resultadoDatoItem.getBonusFuerza(),
								resultadoDatoItem.getBonusDestreza(), resultadoDatoItem.getBonusInteligencia(),
								resultadoDatoItem.getFoto().toString(), resultadoDatoItem.getFotoEquipado());
					}
					i++;
					j++;
				}
				tx.commit();
			}
		}

		catch (Exception e) {
			if (tx != null)
				tx.rollback();
			Servidor.getLog().append("Fallo al intentar actualizar personaje" + System.lineSeparator());
			Servidor.getLog().append(e.getMessage() + System.lineSeparator());
		} finally {
			HibernateUtil.closeSessionAndUnbindFromThread();
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
		PaquetePersonaje dbPersonaje = null;
		int i = 2;
		int j = 0;
		Transaction tx = null;
		Mochila resultadoItemsID;
		HibernateUtil.openSessionAndBindToThread();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		tx = session.beginTransaction();
		try {
			Query queryUsuario = session.createQuery("from PaqueteUsuario where usuario = :userName ");
			queryUsuario.setParameter("userName", user.getUsername());
			PaqueteUsuario dbUser = (PaqueteUsuario) queryUsuario.getSingleResult();

			int idPersonaje = dbUser.getIdPj();

			// Selecciono los datos del personaje
			Query queryPersonaje = session.createQuery("from PaquetePersonaje where idPersonaje = :idPersonaje ");
			queryPersonaje.setParameter("idPersonaje", idPersonaje);
			dbPersonaje = (PaquetePersonaje) queryPersonaje.getSingleResult();

			// Traigo los id de los items correspondientes a mi personaje que se encuentran
			// en su mochila
			Query queryMochila = session.createQuery("FROM Mochila WHERE idMochila = :idMochila");
			queryMochila.setParameter("idMochila", dbPersonaje.getId());
			List<Mochila> listaItems = queryMochila.list();

			
			dbPersonaje.eliminarItems();
			
			// si tiene items le añado los bonus
			if (listaItems != null && !listaItems.isEmpty()) {

				resultadoItemsID = listaItems.get(0);
				
				while (j <= CANTITEMS) {
					if (resultadoItemsID.getItemId(i) != -1) {
						
						
						Query queryitem = session.createQuery("FROM MyItem WHERE idItem = :idItem");
						//busco el item en la tabla items segun su id y me traigo sus valores
						queryitem.setParameter("idItem", resultadoItemsID.getItemId(i));

						MyItem resultadoDatoItem = (MyItem) queryitem.getSingleResult();
						
						//le añado al personaje el item para que lo pueda visualizar 
						dbPersonaje.anadirItem(resultadoDatoItem.getIdItem(), resultadoDatoItem.getNombre(),
								resultadoDatoItem.getWearLocation(), resultadoDatoItem.getBonusSalud(),
								resultadoDatoItem.getBonusEnergia(), resultadoDatoItem.getBonusFuerza(),
								resultadoDatoItem.getBonusDestreza(), resultadoDatoItem.getBonusInteligencia(),
								resultadoDatoItem.getFoto().toString(), resultadoDatoItem.getFotoEquipado());
					}
					i++;
					j++;
				}
				tx.commit();
				return dbPersonaje;
			}
		}

		catch (Exception e) {
			if (tx != null)
				tx.rollback();
			Servidor.getLog().append("Fallo al intentar obtener el personaje" + System.lineSeparator());
			Servidor.getLog().append(e.getMessage() + System.lineSeparator());
		} finally {
			HibernateUtil.closeSessionAndUnbindFromThread();
		}
		return dbPersonaje;

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

		HibernateUtil.openSessionAndBindToThread();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
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
			HibernateUtil.closeSessionAndUnbindFromThread();
			return paqueteUsuario;
		} catch (HibernateException e) {
			Servidor.getLog().append("Fallo al intentar recuperar el usuario " + usuario + System.lineSeparator());
			Servidor.getLog().append(e.getMessage() + System.lineSeparator());
			HibernateUtil.closeSessionAndUnbindFromThread();
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

		HibernateUtil.openSessionAndBindToThread();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		int i = 0;
		
		try {
			
			//creo mochila del id del personaje
			final Mochila mochila = new Mochila(paquetePersonaje.getId());
			
			// por cada item que tiene el personaje seteo la mochila
			for(i=0 ; i<paquetePersonaje.getCantItems();i++) {
				mochila.setItem(i + 1, paquetePersonaje.getItemID(i));
				
			}

			// actualizo inventario de mochila
			session.update(mochila);

			Servidor.getLog().append("El personaje " + paquetePersonaje.getNombre()
					+ " ha actualizado su inventario." + System.lineSeparator());

		} catch (final Exception e) {
			Servidor.getLog().append("Fallo al intentar actualizar el inventario del personaje "
					+ paquetePersonaje.getNombre() + System.lineSeparator());
		} finally {
			HibernateUtil.closeSessionAndUnbindFromThread();
		}

	}

	/**
	 * Actualizar inventario.
	 *
	 * @param idPersonaje
	 *            the id personaje
	 */
	public void actualizarInventario(final int idPersonaje) {
		
		HibernateUtil.openSessionAndBindToThread();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		Transaction tx = null;
		int itemGanado;
		int value = 0;
		
		try {
			PaquetePersonaje paquetePersonaje = Servidor.getPersonajesConectados().get(idPersonaje);
			tx = session.beginTransaction();
			Query query = session
					.createQuery("UPDATE Mochila " + "SET item1 = :it1 ,item2 = :it2 ,item3 = :it3 ,item4 = :it4 ,"
							+ "item5 = :it5 ,item6 = :it6 ,item7 = :it7 ,item8 = :it8 ,"
							+ "item9 = :it9 ,item10 = :it10 ,item11 = :it11 ,item12 = :it12 ,"
							+ "item13 = :it13 ,item14 = :it14 ,item15 = :it15 ,item16 = :it16 ,"
							+ "item17 = :it17 ,item18 = :it18 ,item19 = :it19 ,item20 = :it20"
							+ " WHERE idMochila = :idMochila");

			// actualiazo la mochila segun los items que tenga el personaje
			
			for (int i = 1; i <= paquetePersonaje.getCantItems(); i++) {
				query.setParameter("it" + i, paquetePersonaje.getItemID(i - 1));
			}
			
			
			itemGanado = new Random().nextInt(CANTITEMS + CANTITEMSMOCHILA) + 1;
			
			//seteo el item random ganado en la ultima ranura libre
			if (paquetePersonaje.getCantItems() < CANTITEMS) {
				value = paquetePersonaje.getCantItems() + 1;
				query.setParameter("it" + value, itemGanado);
				paquetePersonaje.anadirItem(itemGanado);
			}
			
			// pongo el resto vacio
			for (int j = paquetePersonaje.getCantItems() + 1; j <= CANTITEMSMOCHILA; j++) {
				query.setParameter("it" + j, -1);
			}
			
			//donde la mochila sea igual a idmochila
			query.setParameter("idMochila", paquetePersonaje.getIdMochila());
			
			query.executeUpdate();
			
			tx.commit();
		}
		catch (Exception e) {
			if (tx != null)
				tx.rollback();
			Servidor.getLog().append("Fallo al intentar actualizar inventario" + System.lineSeparator());
			Servidor.getLog().append(e.getMessage() + System.lineSeparator());
		} finally {
			HibernateUtil.closeSessionAndUnbindFromThread();
		}

		
	}

	/**
	 * Actualizar personaje subio nivel.
	 *
	 * @param paquetePersonaje
	 *            the paquete personaje
	 */
	public void actualizarPersonajeSubioNivel(final PaquetePersonaje paquetePersonaje) {

		HibernateUtil.openSessionAndBindToThread();

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction transaccion = session.beginTransaction();
		try {
			// Actualizo Personaje
			session.update(paquetePersonaje);

			transaccion.commit();

			Servidor.getLog()
					.append("se ha Actualizado el Personaje" + paquetePersonaje.getId() + System.lineSeparator());

		} catch (HibernateException e) {
			if (transaccion != null)
				transaccion.rollback();
			e.printStackTrace();
			Servidor.getLog().append(
					"Error al intentar crear el personaje " + paquetePersonaje.getNombre() + System.lineSeparator());
		} finally {
			HibernateUtil.closeSessionAndUnbindFromThread();
		}

	}
}
