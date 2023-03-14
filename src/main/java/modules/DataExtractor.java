package modules;

import dbModel.Info;
import dbModel.MatchWrapper;
import dbModel.TimelineWrapper;
import dbModel.Timeline;
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
    private static final int DAYS = 1;
    private static final GsonBuilder gsonBuilder = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return Objects.equals(fieldAttributes.getName(), "id");
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return false;
        }
    });
    private static final Gson gson = gsonBuilder.create();

    public static Organization getOrganization(String name) throws URISyntaxException, IOException, InterruptedException {

        Organization organization = new Organization(name, getOrganizationShortcut(name));
        organization.addRoster(getRoster(organization));

        return organization;
    }

    private static String getOrganizationShortcut(String name) throws URISyntaxException, IOException, InterruptedException {

        String jsonString = Network.getJSONFromURLString("https://lol.fandom.com/api.php?action=cargoquery&format=json&tables=Teams&fields=Short&where=Teams.Name='" + name + "'");
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

        return jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("Short").getAsString();
    }

    private static Roster getRoster(Organization org) throws URISyntaxException, IOException, InterruptedException {

        Roster roster = new Roster();
        roster.setOrg(org);

        String jsonString = Network.getJSONFromURLString("https://lol.fandom.com/api.php?action=cargoquery&format=json&tables=TournamentRosters&fields=RosterLinks&where=TournamentRosters.Team='"
                + org.getName() + "' AND TournamentRosters.OverviewPage='" + TOURNAMENT + "'");
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        String rosterString = jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("RosterLinks").getAsString();

        String[] playerArray = rosterString.split(";;");

        roster.setTop(getPlayer(playerArray[0], roster));
        roster.setJungle(getPlayer(playerArray[1], roster));
        roster.setMid(getPlayer(playerArray[2], roster));
        roster.setBot(getPlayer(playerArray[3], roster));
        roster.setSupport(getPlayer(playerArray[4], roster));

        return roster;
    }

    public static void getOrganizationPlayers(Organization org) throws IOException, URISyntaxException, InterruptedException {

        List<Player> players = org.getRoster().getPlayers();

        for (Player p : players) {
            getPlayerAccounts(p);
        }
    }

    private static Player getPlayer(String playerName, Roster roster) {

        Player player = new Player(playerName, roster);
        player.addAccount(new Account(roster.getOrg().getShortcut() + " " + playerName, true, player));

        return player;
    }

    private static void getPlayerAccounts(Player player) throws IOException, URISyntaxException, InterruptedException {

        Document doc = Network.getDocumentFromURLString("https://lolpros.gg/player/" + player.getName());

        if(doc.getElementsByClass("accounts-list").isEmpty()) {
            player.addAccount(getAccount(doc.getElementById("summoner-names").getElementsByTag("p").get(0).text(), player));
        } else {
            Elements accountElements = doc.getElementsByClass("accounts-list").get(0).getElementsByTag("span");
            for (Element e: accountElements) {
                player.addAccount(getAccount(e.text(), player));
            }
        }
    }

    private static Account getAccount(String name, Player player) throws URISyntaxException, IOException, InterruptedException {

        String jsonString = Network.getJSONFromURLString("https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + name);

        Account account = gson.fromJson(jsonString, Account.class);
        account.setPlayer(player);
        return account;
    }

    public static void main(String[] args) {
    }

    public static void getAccountMatches(Account acc) throws URISyntaxException, IOException, InterruptedException {

        if (acc.getCompetitive()) {
            // TODO: 08/02/2023 deal with multiple matches for team
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
                    acc.addMatch(getMatchFromWiki(jsonElement.getAsJsonObject().get("title").getAsJsonObject().get("StatsPage").getAsString(), acc));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            String jsonString = Network.getJSONFromURLString("https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + acc.getPuuid() + "/ids?startTime="
                    + Time.getEpochInSeconds(DAYS) + "&queue=420&count=100");
            String[] listOfMatchIDs = gson.fromJson(jsonString, String[].class);
            for (String id: listOfMatchIDs) {
                acc.addMatch(getMatchFromRiot(id));
            }
        }

    }

    private static Match getMatchFromWiki(String id, Account account) throws IOException {
        Document documentData = Network.getDocumentFromURLString("https://lol.fandom.com/wiki/" + id + "?action=edit");
        String jsonStringData = Objects.requireNonNull(documentData.getElementById("wpTextbox1")).text();
        Info info = gson.fromJson(jsonStringData, Info.class);

        Document documentTimeline = Network.getDocumentFromURLString("https://lol.fandom.com/wiki/" + id + "/Timeline?action=edit");
        String jsonStringTimeline = Objects.requireNonNull(documentTimeline.getElementById("wpTextbox1")).text();
        Timeline timeline = gson.fromJson(jsonStringTimeline, Timeline.class);

        // TODO: 08/02/2023 add metadata creation 

        return new Match(info, timeline, account);
    }

    public static Match getMatchFromRiot(String id) throws URISyntaxException, IOException, InterruptedException {

        String jsonStringData = Network.getJSONFromURLString("https://europe.api.riotgames.com/lol/match/v5/matches/" + id);
        MatchWrapper matchWrapper = gson.fromJson(jsonStringData, MatchWrapper.class);

        String jsonStringTimeline = Network.getJSONFromURLString("https://europe.api.riotgames.com/lol/match/v5/matches/" + id + "/timeline");
        TimelineWrapper timelineWrapper = gson.fromJson(jsonStringTimeline, TimelineWrapper.class);

        return new Match(matchWrapper.getMetadata(), matchWrapper.getInfo(), timelineWrapper.getTimeline());
    }
}
