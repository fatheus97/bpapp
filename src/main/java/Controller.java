import modules.DatabaseManager;
import modules.GUI;

public class Controller {
    public static void main(String[] args) {

        DatabaseManager.openSession();

        GUI gui = new GUI();
    }
}
