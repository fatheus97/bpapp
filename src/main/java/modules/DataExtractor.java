package modules;

import dbModel.*;
import com.google.gson.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataExtractor {
    private static final Properties props = new Properties();
    static {
        try {
            props.load(Network.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static final String TOURNAMENT = props.getProperty("tournament");
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

    private static final List<Match> matches = new ArrayList<>();

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

    public static void updateOrganization(Organization org) throws URISyntaxException, IOException, InterruptedException {
        updateRoster(org);
    }

    private static void updateRoster(Organization org) throws URISyntaxException, IOException, InterruptedException {

        Roster oldRoster = org.getRoster();
        Roster newRoster = new Roster();
        newRoster.setOrg(org);
        List<Player> players = oldRoster.getPlayers();
        int nOfChanges = 0;

        Map<Role, Player> oldRosterMap = players.stream()
                .collect(Collectors.toMap(Player::getRole, Function.identity()));
        Map<Role, String> newRosterMap = getRosterMap(org);

        for (Map.Entry<Role, String> entry : newRosterMap.entrySet()) {
            if (Objects.equals(entry.getValue(), oldRosterMap.get(entry.getKey()).getName())) {
                newRoster.addPlayer(oldRosterMap.get(entry.getKey()));
            } else {
                nOfChanges += 1;
                Player player = DatabaseManager.getObject(Player.class, entry.getValue());
                newRoster.addPlayer(Objects.requireNonNullElseGet(player, () -> getPlayer(entry.getValue(), entry.getKey(), newRoster)));
            }
        }

        if (nOfChanges > 0) {
            newRoster.setNOfChanges(nOfChanges);
            org.addRoster(newRoster);
        }
    }

    private static Map<Role, String> getRosterMap(Organization org) throws URISyntaxException, IOException, InterruptedException {
        
        String urlString = "https://lol.fandom.com/api.php?action=cargoquery" +
                "&format=json" +
                "&tables=TournamentRosters" +
                "&fields=RosterLinks,Roles" +
                "&where=TournamentRosters.Team='" + org.getName() +
                "' AND TournamentRosters.OverviewPage='" + TOURNAMENT + "'";

        String jsonString = Network.getJSONFromURLString(urlString);
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        String playersNameString = jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("RosterLinks").getAsString();
        String playersRoleString= jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("Roles").getAsString();

        String[] playersNameArray = playersNameString.split(";;");
        String[] playersRoleArray = playersRoleString.split(";;");

        return IntStream.range(0, playersNameArray.length)
                .boxed()
                .filter(i -> Arrays.stream(Role.values())
                        .map(Enum::name)
                        .toList()
                        .contains(playersRoleArray[i].toUpperCase()))
                .collect(Collectors.toMap(i -> Role.valueOf(playersRoleArray[i].toUpperCase()), i -> playersNameArray[i]));
    }

    private static Roster getRoster(Organization org) throws URISyntaxException, IOException, InterruptedException {

        Roster roster = new Roster();
        roster.setOrg(org);

        Map<Role, String> rosterMap = getRosterMap(org);

        for (Map.Entry<Role, String> entry : rosterMap.entrySet()) {
            roster.addPlayer(getPlayer(entry.getValue(), entry.getKey(), roster));
        }

        return roster;
    }
    
    private static Player getPlayer(String playerName, Role role, Roster roster) {

        Player player = new Player(playerName, role, roster);
        player.addAccount(new Account(roster.getOrg().getShortcut() + " " + playerName, true, player));

        return player;
    }
    
    public static void fetchAccountsToPlayers(Organization org) throws IOException, URISyntaxException, InterruptedException {

        List<Player> players = org.getRoster().getPlayers();

        for (Player p : players) {
            System.out.println(p);
            fetchAccountsToPlayer(p);
        }
    }

    private static void fetchAccountsToPlayer(Player player) throws IOException, URISyntaxException, InterruptedException {

        Set<String> oldAccountNames = player.getAccounts().stream()
                .map(Account::getName)
                .collect(Collectors.toSet());
        Set<String> newAccountNames = getAccountNamesSet();

        Document doc = Network.getDocumentFromURLString("https://lolpros.gg/player/" + player.getName());

        if(doc.getElementsByClass("accounts-list").isEmpty()) {
            player.addAccount(getAccount(Objects.requireNonNull(doc.getElementById("summoner-names")).getElementsByTag("p").get(0).text(), player));
        } else {
            Elements accountElements = doc.getElementsByClass("accounts-list").get(0).getElementsByTag("span");
            for (Element e: accountElements) {
                player.addAccount(getAccount(e.text(), player));
            }
        }

        oldAccountNames.stream()
                .filter(val -> !newAccountNames.contains(val))
                .forEach(val -> doSomething(val));

        newAccountNames.stream()
                .filter(val -> !oldAccountNames.contains(val))
                .forEach(val -> doSomethingElse(val));
    }

    private static Set<String> getAccountNamesSet() {
        
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
            String urlString = "https://lol.fandom.com/api.php?action=cargoquery" +
                    "&format=json" +
                    "&tables=ScoreboardPlayers=SP,ScoreboardGames=SG,PostgameJsonMetadata=PJM" +
                    "&join_on=SP.GameId=SG.GameId,SG.RiotPlatformGameId=PJM.RiotPlatformGameId" +
                    "&fields=PJM.StatsPage" +
                    "&where=SP.Name='" + acc.getName().substring(acc.getName().indexOf(" ")+1) +
                    "' AND SP.DateTime_UTC>" + Time.getUTCString(DAYS, DateTimeFormatter.ofPattern("yyyyMMddhhmmss"));
            System.out.println(urlString);
            String jsonString = Network.getJSONFromURLString(urlString);
            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            jsonObject.get("cargoquery").getAsJsonArray().forEach(jsonElement -> {
                String matchID = jsonElement.getAsJsonObject().get("title").getAsJsonObject().get("StatsPage").getAsString();
                Match match = returnMatch(matchID);
                if (match == null) {
                    try {
                        match = getMatchFromWiki(matchID, acc);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    matches.add(match);
                }
                System.out.println(acc + " " + match);
                acc.addMatch(match);
            });
        } else {
            String jsonString = Network.getJSONFromURLString("https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + acc.getPuuid() + "/ids?startTime="
                    + Time.getEpochInSeconds(DAYS) + "&queue=420&count=100");
            String[] listOfMatchIDs = gson.fromJson(jsonString, String[].class);
            for (String matchID: listOfMatchIDs) {
                Match match = returnMatch(matchID);
                if (match == null) {
                    match = getMatchFromRiot(matchID, acc);
                    matches.add(match);
                }
                System.out.println(match);
                acc.addMatch(match);
            }
        }

    }

    private static Match returnMatch(String matchId) {
        for (Match match : matches) {
            if (match.getMatchID().equals(matchId)) {
                return match;
            }
        }
        return null;
    }

    private static Match getMatchFromWiki(String id, Account account) throws IOException {
        Document documentInfo = Network.getDocumentFromURLString("https://lol.fandom.com/wiki/" + id + "?action=edit");
        String jsonStringInfo = Objects.requireNonNull(documentInfo.getElementById("wpTextbox1")).text();
        Info info = gson.fromJson(jsonStringInfo, Info.class);

        Document documentTimeline = Network.getDocumentFromURLString("https://lol.fandom.com/wiki/" + id + "/Timeline?action=edit");
        String jsonStringTimeline = Objects.requireNonNull(documentTimeline.getElementById("wpTextbox1")).text();
        Timeline timeline = gson.fromJson(jsonStringTimeline, Timeline.class);

        return new Match(id, info, timeline, account);
    }

    public static Match getMatchFromRiot(String id, Account account) throws URISyntaxException, IOException, InterruptedException {

        String jsonStringData = Network.getJSONFromURLString("https://europe.api.riotgames.com/lol/match/v5/matches/" + id);
        MatchWrapper matchWrapper = gson.fromJson(jsonStringData, MatchWrapper.class);

        String jsonStringTimeline = Network.getJSONFromURLString("https://europe.api.riotgames.com/lol/match/v5/matches/" + id + "/timeline");
        TimelineWrapper timelineWrapper = gson.fromJson(jsonStringTimeline, TimelineWrapper.class);

        return new Match(id, matchWrapper.getInfo(), timelineWrapper.getTimeline(), account);
    }


}
