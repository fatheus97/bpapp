import dbModel.Organization;
import dbModel.Player;
import modules.DataExtractor;
import modules.DataInserter;
import modules.GUI;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class Controller {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        Organization org = DataExtractor.getOrganization("MAD Lions");

        GUI.showData(org);

        DataExtractor.getOrganizationPlayers(org);

        List<Player> players = org.getRoster().getPlayers();

        for (Player p : players) {
            GUI.showData(p);
        }

        org.getRoster().getPlayers().forEach(player -> {
            player.getAccounts().forEach(acc -> {
                try {
                    DataExtractor.getAccountMatches(acc);
                } catch (URISyntaxException | IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        DataInserter.openSessionFactory();
        DataInserter.insertObject(org);
        DataInserter.closeSessionFactory();

    }
}
