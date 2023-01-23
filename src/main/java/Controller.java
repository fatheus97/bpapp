import dbModel.Organization;
import dbModel.Player;
import modules.DataExtractor;
import modules.GUI;

import java.util.List;

public class Controller {

    private final DataExtractor dataExtractor = new DataExtractor();
    private final GUI gui = new GUI();

    public static void main(String[] args) {

        new Controller();

    }

    public Controller() {

        Organization org = dataExtractor.getOrganization("MAD Lions");

        gui.showData(org);

        org = dataExtractor.getOrganizationPlayers(org);

        System.out.println(org);

        List<Player> players = org.getRoster().getPlayers();

        for (Player p : players) {
            gui.showData(p);
        }

        org = dataExtractor.getOrganizationSoloQMatches(org);

    }
}
