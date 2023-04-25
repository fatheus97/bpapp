package components;

import dbModel.Insertable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DatabaseManager {
    private static final Configuration configuration = new Configuration().configure();
    private static final SessionFactory sessionFactory = configuration.buildSessionFactory();
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
        sessionFactory.close();
    }

    public static <T> T getObject(Class<T> objectClass, String objectID) {
        if (session == null)
            session = sessionFactory.openSession();
        return session.get(objectClass, objectID);
    }
}
