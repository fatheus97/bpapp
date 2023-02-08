package modules;

import apiModel.matchData.Data;
import apiModel.matchData.MatchData;
import apiModel.matchTimeline.MatchTimeline;
import apiModel.matchTimeline.Timeline;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class DataExtractor {
    private static final String TOURNAMENT = "LEC/2023 Season/Winter Season";
    private static final int DAYS = 7;
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

    private Player getPlayer(String playerName, Organization org) {

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

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        DataExtractor dataExtractor = new DataExtractor();
        Account acc = new Account("FNC Humanoid", true);
        dataExtractor.getAccountMatches(acc);
        System.out.println(acc);
    }

    public void getAccountMatches(Account acc) throws URISyntaxException, IOException, InterruptedException {

        if (acc.getCompetitive()) {
            // TODO: 08/02/2023 deal with mutliple matches for team 
            String jsonString = Network.getJSONFromURLString("https://lol.fandom.com/api.php?action=cargoquery" +
                    "&format=json" +
                    "&tables=ScoreboardPlayers=SP,ScoreboardGames=SG,PostgameJsonMetadata=PJM" +
                    "&join_on=SP.GameId=SG.GameId,SG.RiotPlatformGameId=PJM.RiotPlatformGameId" +
                    "&fields=PJM.StatsPage" +
                    "&where=SP.Name='" + acc.getName().substring(acc.getName().indexOf(" ")+1) +
                    "' AND SP.DateTime_UTC>" + Time.getUTCString(DAYS, DateTimeFormatter.ofPattern("yyyyMMddhhmmss")));
            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            jsonObject.get("cargoquery").getAsJsonArray().forEach(jsonElement -> {
                try {
                    acc.addMatch(getMatchFromWiki(jsonElement.getAsJsonObject().get("title").getAsJsonObject().get("StatsPage").getAsString()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            String jsonString = Network.getJSONFromURLString("https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + acc.getPuuid() + "/ids?startTime="
                    + Time.getEpochInSeconds(DAYS) + "&queue=420&count=100");
            String[] listOfMatchIDs = gson.fromJson(jsonString, String[].class);
            for (String id: listOfMatchIDs) {
                acc.addMatch(this.getMatchFromRiot(id));
            }
        }

    }

    private Match getMatchFromWiki(String id) throws IOException {
        Document documentData = Network.getDocumentFromURLString("https://lol.fandom.com/wiki/" + id + "?action=edit");
        String jsonStringData = Objects.requireNonNull(documentData.getElementById("wpTextbox1")).text();
        Data data = gson.fromJson(jsonStringData, Data.class);

        Document documentTimeline = Network.getDocumentFromURLString("https://lol.fandom.com/wiki/" + id + "/Timeline?action=edit");
        String jsonStringTimeline = Objects.requireNonNull(documentTimeline.getElementById("wpTextbox1")).text();
        Timeline timeline = gson.fromJson(jsonStringTimeline, Timeline.class);

        // TODO: 08/02/2023 add metadata creation 

        return new Match(data, timeline);
    }

    public Match getMatchFromRiot(String id) throws URISyntaxException, IOException, InterruptedException {

        String jsonStringData = Network.getJSONFromURLString("https://europe.api.riotgames.com/lol/match/v5/matches/" + id);
        MatchData matchData = gson.fromJson(jsonStringData, MatchData.class);

        String jsonStringTimeline = Network.getJSONFromURLString("https://europe.api.riotgames.com/lol/match/v5/matches/" + id + "/timeline");
        MatchTimeline matchTimeline = gson.fromJson(jsonStringTimeline, MatchTimeline.class);

        return new Match(matchData.getMetadata(), matchData.getData(), matchTimeline.getTimeline());
    }
}
