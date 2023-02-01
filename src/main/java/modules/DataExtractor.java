package modules;

import apiModel.matchData.MatchData;
import apiModel.matchTimeline.MatchTimeline;
import dbModel.Match;
import com.google.gson.*;
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
    private static final String TOURNAMENT = "LEC/2023 Season/Winter Season";
    private static final int DAYS = 1;
    private final Gson gson;

    public DataExtractor() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        this.gson = gsonBuilder.create();
    }

    public Organization getOrganization(String name) throws URISyntaxException, IOException, InterruptedException {

        Organization organization = new Organization(name, getOrganizationShortcut(name));
        Roster roster = getRoster(organization);
        organization.setRoster(roster);

        return organization;
    }

    private String getOrganizationShortcut(String name) throws URISyntaxException, IOException, InterruptedException {

        String jsonString = Network.getJSONFromURLString("https://lol.fandom.com/api.php?action=cargoquery&format=json&tables=Teams&fields=Short&where=Teams.Name='" + name + "'");
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

        return jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("Short").getAsString();
    }

    private Roster getRoster(Organization org) throws URISyntaxException, IOException, InterruptedException {

        Roster roster = new Roster();

        String jsonString = Network.getJSONFromURLString("https://lol.fandom.com/api.php?action=cargoquery&format=json&tables=TournamentRosters&fields=RosterLinks&where=TournamentRosters.Team='"
                + org.getName() + "' AND TournamentRosters.OverviewPage='" + TOURNAMENT + "'");
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        String rosterString = jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("RosterLinks").getAsString();

        String[] playerArray = rosterString.split(";;");

        roster.setTop(getPlayer(playerArray[0], org));
        roster.setJungle(getPlayer(playerArray[1], org));
        roster.setMid(getPlayer(playerArray[2], org));
        roster.setBot(getPlayer(playerArray[3], org));
        roster.setSupport(getPlayer(playerArray[4], org));

        return roster;
    }

    public Organization getOrganizationPlayers(Organization org) throws IOException, URISyntaxException, InterruptedException {

        List<Player> players = org.getRoster().getPlayers();

        for (Player p : players) {
            getPlayerAccounts(p);
        }

        return org;
    }

    private Player getPlayer(String playerName, Organization org) throws URISyntaxException, IOException, InterruptedException {

        Player player = new Player(playerName);
        player.addAccount(new Account(org.getShortcut() + " " + playerName, true));

        return player;
    }

    private void getPlayerAccounts(Player player) throws IOException, URISyntaxException, InterruptedException {

        Document doc = Network.getDocumentFromURLString("https://lolpros.gg/player/" + player.getName());

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

        String jsonString = Network.getJSONFromURLString("https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + name);

        return gson.fromJson(jsonString, Account.class);
    }

    public void getAccountMatches(Account acc) throws URISyntaxException, IOException, InterruptedException {

        if (acc.getCompetitive()) {
            // TODO: 01/02/2023 get competitive account matches
            String url = "https://lol.fandom.com/api.php?action=cargoquery&format=json&tables=ScoreboardPlayers&fields=GameId,DateTime_UTC&where=Name=%22" + acc.getName().substring(acc.getName().indexOf(" ")+1) +  "%22+AND+DateTime_UTC%3E%27" + LocalDateTime.now().minusDays(DAYS) + "%27";
        } else {
            String jsonString = Network.getJSONFromURLString("https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + acc.getPuuid() + "/ids?startTime="
                    + Time.getTimestampInSeconds(DAYS) + "&queue=420&count=100");
            String[] listOfMatchIDs = gson.fromJson(jsonString, String[].class);
            for (String id: listOfMatchIDs) {
                acc.addMatch(this.getMatchFromRiot(id));
            }
        }
    }

    public Match getMatchFromRiot(String id) throws URISyntaxException, IOException, InterruptedException {

        String jsonStringData = Network.getJSONFromURLString("https://europe.api.riotgames.com/lol/match/v5/matches/" + id);
        MatchData matchData = gson.fromJson(jsonStringData, MatchData.class);

        String jsonStringTimeline = Network.getJSONFromURLString("https://europe.api.riotgames.com/lol/match/v5/matches/" + id + "/timeline");
        MatchTimeline matchTimeline = gson.fromJson(jsonStringTimeline, MatchTimeline.class);

        return new Match(matchData, matchTimeline);
    }
}
