
package hibernatePackage;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ThreadLocalSessionContext;

/**
 * The Class HibernateUtil.
 */
public class HibernateUtil {
	
	 /**
 	 * Open thread session.
 	 *
 	 * @param session the session
 	 */
 	public static void openThreadSession(Session session) { 
	        ThreadLocalSessionContext.bind(session); 
	 } 
	     
	 /**
 	 * Close thread session.
 	 *
 	 * @param session the session
 	 * @param factory the factory
 	 */
 	public static void closeThreadSession(Session session , SessionFactory factory) { 
	        session = ThreadLocalSessionContext.unbind(factory); 
	        if (session!=null) 
	        session.close(); 
	        factory.close();
	 } 
}

