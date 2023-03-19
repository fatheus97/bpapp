package modules;

import dbModel.Insertable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DatabaseManager {
    private static final Configuration configuration = new Configuration().configure();
    private static final SessionFactory sessionFactory = configuration.buildSessionFactory();

    private static Session session;

    public static void main(String[] args) {

    }

    public static void insertObject(Insertable obj) {
        // Begin a new transaction
        Transaction transaction = session.beginTransaction();

        // Save the match object to the database
        session.saveOrUpdate(obj);

        // Commit the transaction
        transaction.commit();

        // Close the session
        session.close();
    }

    public static void openSession() {
        session = sessionFactory.openSession();
    }
    public static void closeSession() {
        session.close();
    }
    public static void closeSessionFactory() {
        sessionFactory.close();
    }
    public static <T> T getObject(Class<T> objectClass, String objectID) {
        return session.get(objectClass, objectID);
    }
}
