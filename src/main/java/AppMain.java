import components.DatabaseManager;
import components.MainFrame;

public class AppMain {
    public static void main(String[] args) {
        DatabaseManager.openSession();

        MainFrame mainFrame = new MainFrame();
    }
}
