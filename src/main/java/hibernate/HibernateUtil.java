package hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.internal.ThreadLocalSessionContext;

import servidor.Servidor;

public class HibernateUtil {

	private static  SessionFactory sessionFactory;

	public static synchronized void buildSessionFactory() {
		if (sessionFactory == null) {
			try {
			Configuration configuration = new Configuration();
			configuration.configure("hibernate.cfg.xml");
			configuration.setProperty("hibernate.current_session_context_class", "thread");
			sessionFactory = configuration.buildSessionFactory();
			Servidor.getLog().append("Inicializando SessionFactory..." + System.lineSeparator());
			}
				catch (HibernateException he) {
				Servidor.getLog().append("Ocurrió un error en la inicialización de la SessionFactory: " + he);
				throw new ExceptionInInitializerError(he);
			}
		}
	}
	


	public static void openSessionAndBindToThread() {
		Session session = sessionFactory.openSession();
		ThreadLocalSessionContext.bind(session);
	}

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			buildSessionFactory();
		}
		return sessionFactory;
	}

	public static void closeSessionAndUnbindFromThread() {
		Session session = ThreadLocalSessionContext.unbind(sessionFactory);
		if (session != null) {
			session.close();
		}
	}

	public static void closeSessionFactory() {
		if ((sessionFactory != null) && (sessionFactory.isClosed() == false)) {
		}
	}
}
