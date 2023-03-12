package modules;

import dbModel.Insertable;
import dbModel.Player;
import dbModel.Roster;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DataInserter {
    private static final Configuration configuration = new Configuration().configure();
    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        openSessionFactory();
        insertObject(new Player("Rigi", new Roster()));
    }

    public static void insertObject(Insertable obj) {
        Session session = sessionFactory.openSession();

        // Begin a new transaction
        Transaction transaction = session.beginTransaction();

        // Save the match object to the database
        session.save(obj);

        // Commit the transaction
        transaction.commit();

        // Close the session
        session.close();
    }

    public static void openSessionFactory() {
        sessionFactory = configuration.buildSessionFactory();
    }

    public static void closeSessionFactory() {
        sessionFactory.close();
    }
}
