import dbModel.Organization;
import dbModel.Player;
import modules.DataExtractor;
import modules.GUI;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class Controller {

    private final DataExtractor dataExtractor = new DataExtractor();
    private final GUI gui = new GUI();

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        new Controller();

    }

    public Controller() throws URISyntaxException, IOException, InterruptedException {

        Organization org = dataExtractor.getOrganization("MAD Lions");

        gui.showData(org);

        org = dataExtractor.getOrganizationPlayers(org);

        List<Player> players = org.getRoster().getPlayers();

        for (Player p : players) {
            gui.showData(p);
        }

        org.getRoster().getPlayers().forEach(player -> {
            player.getAccounts().forEach(acc -> {
                try {
                    dataExtractor.getAccountMatches(acc);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        System.out.println(org);
    }
}
