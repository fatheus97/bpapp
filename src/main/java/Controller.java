import modules.DatabaseManager;
import modules.GUI;
import org.jsoup.nodes.Document;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Controller {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        DatabaseManager.openSession();

        GUI gui = new GUI();
    }
}
