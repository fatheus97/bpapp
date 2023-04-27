package components;

import dbModel.Insertable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.spi.ServiceException;

public class DatabaseManager {
    private static final Configuration CONFIGURATION = new Configuration().configure();
    private static SessionFactory sessionFactory;
    private static Session session;

    private DatabaseManager() {
    }

    public static void insertObject(Insertable obj) {
        Transaction transaction = session.beginTransaction();
        session.merge(obj);
        transaction.commit();
    }

    public static void closeSessionFactory() {
        if (session != null)
            session.close();
        if (sessionFactory != null)
            sessionFactory.close();
    }

    public static <T> T getObject(Class<T> objectClass, String objectID) throws IllegalStateException, ServiceException {
        if (sessionFactory == null) {
            sessionFactory = CONFIGURATION.buildSessionFactory();
            session = sessionFactory.openSession();
        }
        return session.get(objectClass, objectID);
    }
}
