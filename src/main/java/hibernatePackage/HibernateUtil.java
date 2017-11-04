package hibernatePackage;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ThreadLocalSessionContext;

public class HibernateUtil {
	
	 public static void openThreadSession(Session session) { 
	        ThreadLocalSessionContext.bind(session); 
	 } 
	     
	 public static void closeThreadSession(Session session , SessionFactory factory) { 
	        session = ThreadLocalSessionContext.unbind(factory); 
	        if (session!=null) 
	        session.close(); 
	        factory.close();
	 } 
}

