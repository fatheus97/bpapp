package modules;

import com.google.gson.*;
import apiModel.*;
import dbModel.Account;
import dbModel.Organization;
import dbModel.Player;
import dbModel.Roster;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

public class DataExtractor {
    private static final String TOURNAMENT = "LEC/2023+Season/Winter+Season";
    private static final int DAYS = 7;
    private final Gson gson;

    public DataExtractor() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        this.gson = gsonBuilder.create();
    }

    public Organization getOrganization(String name) throws URISyntaxException, IOException, InterruptedException {

        Organization organization = new Organization(name, getOrganizationShortcut(name));
        Roster roster = getRoster(name);
        organization.setRoster(roster);

        return organization;
    }

    private String getOrganizationShortcut(String name) throws URISyntaxException, IOException, InterruptedException {

        String codedName = Utils.encodeString(name);
        String url = "https://lol.fandom.com/api.php?action=cargoquery&format=json&tables=Teams&fields=Short&where=Teams.Name=%27" + codedName + "%27";
        String jsonString = Utils.getJSONFromURL(url);
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

        return jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("Short").getAsString();
    }

    private Roster getRoster(String name) throws URISyntaxException, IOException, InterruptedException {

        Roster roster = new Roster();

        String codedName = Utils.encodeString(name);
        String url = "https://lol.fandom.com/api.php?action=cargoquery&format=json&tables=TournamentRosters&fields=RosterLinks&where=TournamentRosters.Team=%27" + codedName + "%27+AND+TournamentRosters.OverviewPage=%27" + TOURNAMENT + "%27";
        String jsonString = Utils.getJSONFromURL(url);
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        String rosterString = jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("RosterLinks").getAsString();

        String[] playerArray = rosterString.split(";;");

        roster.setTop(getPlayer(playerArray[0], name));
        roster.setJungle(getPlayer(playerArray[1], name));
        roster.setMid(getPlayer(playerArray[2], name));
        roster.setBot(getPlayer(playerArray[3], name));
        roster.setSupport(getPlayer(playerArray[4], name));

        return roster;
    }

    public Organization getOrganizationPlayers(Organization org) throws IOException, URISyntaxException, InterruptedException {

        List<Player> players = org.getRoster().getPlayers();

        for (Player p : players) {
            getPlayerAccounts(p);
        }

        return org;
    }

    private Player getPlayer(String playerName, String orgName) throws URISyntaxException, IOException, InterruptedException {

        Player player = new Player(playerName);
        player.addAccount(new Account(getOrganizationShortcut(orgName) + " " + playerName, true));

        return player;
    }

    private void getPlayerAccounts(Player player) throws IOException, URISyntaxException, InterruptedException {

        String codedName = Utils.encodeString(player.getName());
        String url = "https://lolpros.gg/player/" + codedName;
        Document doc = Utils.getDocumentFromURL(url);

        if(doc.getElementsByClass("accounts-list").isEmpty()) {
            player.addAccount(getAccount(doc.getElementById("summoner-names").getElementsByTag("p").get(0).text()));
        } else {
            Elements accountElements = doc.getElementsByClass("accounts-list").get(0).getElementsByTag("span");
            for (Element e: accountElements) {
                player.addAccount(getAccount(e.text()));
            }
        }
    }

    private Account getAccount(String name) throws URISyntaxException, IOException, InterruptedException {

        String codedName = Utils.encodeString(name);
        String url = "https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + codedName;
        String jsonString = Utils.getJSONFromURL(url);

        Account account = gson.fromJson(jsonString, Account.class);

        return account;
    }

    public void getAccountMatches(Account acc) throws URISyntaxException, IOException, InterruptedException {

        if (acc.getCompetitive()) {
            String url = "https://lol.fandom.com/api.php?action=cargoquery&format=json&tables=ScoreboardPlayers&fields=GameId,DateTime_UTC&where=Name=%22" + acc.getName().substring(acc.getName().indexOf(" ")+1) +  "%22+AND+DateTime_UTC%3E%27" + LocalDateTime.now().minusDays(DAYS) + "%27";
            System.out.println(url);
        } else {
            String url = "https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + acc.getPuuid() + "/ids?startTime=" + Utils.getTimestampInSeconds(DAYS) + "&queue=420&count=100";
            String jsonString = Utils.getJSONFromURL(url);
            String[] listOfMatchIDs = gson.fromJson(jsonString, String[].class);
            for (String id: listOfMatchIDs) {
                acc.addMatch(this.getMatch(id));
            }
        }
    }

    public Match getMatch(String id) throws URISyntaxException, IOException, InterruptedException {

        String codedID = Utils.encodeString(id);

        String url = "https://europe.api.riotgames.com/lol/match/v5/matches/" + codedID;
        String jsonString = Utils.getJSONFromURL(url);

        return gson.fromJson(jsonString, Match.class);
    }
}
