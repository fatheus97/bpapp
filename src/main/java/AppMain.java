import modules.DatabaseManager;
import modules.GUI;

public class AppMain {
    public static void main(String[] args) {
        DatabaseManager.openSession();

        GUI gui = new GUI();
    }
}
