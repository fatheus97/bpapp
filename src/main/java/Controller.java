import dbModel.Organization;
import dbModel.Player;
import modules.DataExtractor;
import modules.DatabaseManager;
import modules.GUI;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class Controller {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        String orgName = "Fnatic";

        DatabaseManager.openSession();

        Organization org = DatabaseManager.getObject(Organization.class, orgName);

        if (org != null){
            DataExtractor.updateOrganization(org);
        } else {
            org = DataExtractor.getOrganization(orgName);
        }

        GUI.showData(org);

        DataExtractor.fetchAccountsToPlayers(org);

            List<Player> players = org.getRoster().getPlayers();

            for (Player p : players) {
                GUI.showData(p);
            }

            players.forEach(player -> {
                player.getAccounts().forEach(acc -> {
                    try {
                        DataExtractor.getAccountMatches(acc);
                    } catch (URISyntaxException | IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            });


        DatabaseManager.insertObject(org);

        DatabaseManager.closeSession();
        DatabaseManager.closeSessionFactory();

    }
}
