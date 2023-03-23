package modules;

import dbModel.*;
import com.google.gson.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DataExtractor {
    private static final Properties props = new Properties();
    static {
        try {
            props.load(Network.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String tournament;
    private static final int START_DAYS = 3;
    private static final int END_DAYS = 1;
    private static final GsonBuilder gsonBuilder = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return Objects.equals(fieldAttributes.getName(), "id")
                    || fieldAttributes.getDeclaredClass() == LocalDateTime.class;
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return false;
        }
    });
    private static final Gson gson = gsonBuilder.create();

    private static final List<Match> matches = new ArrayList<>();

    public static void setTournament(String tournament) {
        DataExtractor.tournament = tournament;
    }

    public static Organization getOrganization(String name) throws URISyntaxException, IOException, InterruptedException {

        Organization organization = new Organization(name, getOrganizationShortcut(name));
        organization.addRoster(getRoster(organization));

        return organization;
    }

    private static String getOrganizationShortcut(String name) throws URISyntaxException, IOException, InterruptedException {

        if(name.contains("(")){
            name = name.substring(0,name.indexOf("("));
        }

        String urlString = "https://lol.fandom.com/api.php?action=cargoquery" +
                "&format=json" +
                "&tables=Teams" +
                "&fields=Short" +
                "&where=Teams.Name='" + name + "'";
        String jsonString = Network.getJSONFromURLString(urlString);
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

        return jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("Short").getAsString();
    }

    public static void updateOrganization(Organization org) throws URISyntaxException, IOException, InterruptedException {
        updateRoster(org);
    }

    private static void updateRoster(Organization org) throws URISyntaxException, IOException, InterruptedException {

        boolean changed = false;

        List<Player> oldPlayers = org.getLastRoster().getPlayers();
        Roster newRoster = getRoster(org);

        if(oldPlayers.size() != newRoster.getPlayers().size())
            changed = true;
        else {
            Map<String, Role> oldRosterMap = oldPlayers.stream()
                    .collect(Collectors.toMap(Player::getName, Player::getRole));
            Map<String, Role> newRosterMap = newRoster.getPlayers().stream()
                    .collect(Collectors.toMap(Player::getName, Player::getRole));

            for (String s : newRosterMap.keySet()) {
                if(!oldRosterMap.containsKey(s))
                    changed = true;
                else if(newRosterMap.get(s) != oldRosterMap.get(s))
                    changed = true;
            }
        }

        if (changed) {
            org.addRoster(newRoster);
        }
    }

    private static Roster getRoster(Organization org) throws URISyntaxException, IOException, InterruptedException {

        String urlString = "https://lol.fandom.com/api.php?action=cargoquery" +
                "&format=json" +
                "&tables=TournamentRosters" +
                "&fields=RosterLinks,Roles" +
                "&where=TournamentRosters.Team='" + org.getName() +
                "' AND TournamentRosters.OverviewPage='" + tournament + "'";

        String jsonString = Network.getJSONFromURLString(urlString);
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        String playersNameString = jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("RosterLinks").getAsString();
        String playersRoleString= jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("Roles").getAsString();

        String[] playersNameArray = playersNameString.split(";;");
        String[] playersRoleArray = playersRoleString.split(";;");

        Roster roster = new Roster();
        roster.setOrg(org);

        for (int i = 0; i < playersNameArray.length; i++) {
            if (!playersRoleArray[i].equalsIgnoreCase("coach")) {
                Role role = Role.valueOf(playersRoleArray[i].toUpperCase());
                String name = playersNameArray[i];
                roster.addPlayer(getPlayer(name, role , roster));
            }
        }

        return roster;
    }
    
    private static Player getPlayer(String playerName, Role role, Roster roster) {

        Player player = new Player(playerName, role, roster);
        player.addAccount(new Account(roster.getOrg().getShortcut() + " " + playerName, true, player));

        return player;
    }
    
    public static void fetchAccountsToPlayers(Organization org) throws IOException, URISyntaxException, InterruptedException {
        List<Player> players = org.getLastRoster().getPlayers();
        for (Player player : players) {
            fetchAccountsToPlayer(player);
        }
    }

    private static void fetchAccountsToPlayer(Player player) throws IOException, URISyntaxException, InterruptedException {

        Set<String> oldAccountsPUUIDs = player.getAccounts().stream()
                .filter(account -> !account.isCompetitive())
                .map(Account::getPuuid)
                .collect(Collectors.toSet());
        List<Account> newAccounts= getAccounts(player);
        Set<String> newAccountsPUUIDs = newAccounts.stream()
                .map(Account::getPuuid)
                .collect(Collectors.toSet());;

        oldAccountsPUUIDs.stream()
                .filter(puuid -> !newAccountsPUUIDs.contains(puuid))
                .forEach(player::deleteAccount);

        newAccounts.stream()
                .filter(account -> !oldAccountsPUUIDs.contains(account.getPuuid()))
                .forEach(player::addAccount);
    }

    private static List<Account> getAccounts(Player player) throws IOException, URISyntaxException, InterruptedException {
        List<Account> accounts = new ArrayList<>();
        Document doc = Network.getDocumentFromURLString("https://lolpros.gg/player/" + player.getName());

        if(doc.getElementsByClass("accounts-list").isEmpty()) {
            accounts.add(getAccount(Objects.requireNonNull(doc.getElementById("summoner-names")).getElementsByTag("p").get(0).text(), player));
        } else {
            Elements accountElements = doc.getElementsByClass("accounts-list").get(0).getElementsByTag("span");
            for (Element e: accountElements) {
                accounts.add(getAccount(e.text(), player));
            }
        }
        return accounts;
    }

    private static Account getAccount(String name, Player player) throws URISyntaxException, IOException, InterruptedException {

        String jsonString = Network.getJSONFromURLString("https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + name);
        System.out.println(jsonString);
        Account account = gson.fromJson(jsonString, Account.class);
        account.setPlayer(player);
        return account;
    }

    public static void fetchMatchesToAccounts(Organization org) {
        org.getLastRoster().getPlayers().forEach(player -> player.getAccounts().forEach(account -> {
            try {
                fetchMatchesToAccount(account, 0);
            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public static void fetchMatchesToAccount(Account account, int start) throws URISyntaxException, IOException, InterruptedException {
        matches.addAll(account.getMatches());
        if (account.isCompetitive()) {
            String lastUpdateTime;
            if(account.getLastUpdated() != null){
                lastUpdateTime = account.getLastUpdated().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss"));
            } else {
                lastUpdateTime = "0";
            }
            String startTime = Time.getUTCString(START_DAYS, DateTimeFormatter.ofPattern("yyyyMMddhhmmss"));
            String endTime = Time.getUTCString(END_DAYS, DateTimeFormatter.ofPattern("yyyyMMddhhmmss"));

            String urlString = "https://lol.fandom.com/api.php?action=cargoquery" +
                    "&format=json" +
                    "&tables=ScoreboardPlayers=SP,ScoreboardGames=SG,PostgameJsonMetadata=PJM" +
                    "&join_on=SP.GameId=SG.GameId,SG.RiotPlatformGameId=PJM.RiotPlatformGameId" +
                    "&fields=PJM.StatsPage" +
                    "&where=SP.Name='" + account.getName().substring(account.getName().indexOf(" ")+1) +
                    //"' AND SP.DateTime_UTC>" + lastUpdateTime +
                    "' AND SP.DateTime_UTC>" + startTime +
                    " AND SP.DateTime_UTC<" + endTime;

            String jsonString = Network.getJSONFromURLString(urlString);
            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            jsonObject.get("cargoquery").getAsJsonArray().forEach(jsonElement -> {
                String matchID = jsonElement.getAsJsonObject()
                        .get("title").getAsJsonObject()
                        .get("StatsPage").getAsString();
                Match match = returnMatch(matchID);
                if (match == null) {
                    try {
                        match = getMatchFromWiki(matchID, account);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    matches.add(match);
                }
                account.addMatch(match);
            });
        } else {
            String jsonString = Network.getJSONFromURLString("https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + account.getPuuid() +
                    "/ids?startTime=" + Time.getEpochInSeconds(START_DAYS) +
                    "&endTime=" + Time.getEpochInSeconds(END_DAYS) +
                    "&queue=420" +
                    "&start=" + start +
                    "&count=100"
            );
            String[] listOfMatchIDs = gson.fromJson(jsonString, String[].class);
            if(listOfMatchIDs.length >= 100) {
                fetchMatchesToAccount(account, start + 100);
            }
            for (String matchID: listOfMatchIDs) {
                Match match = returnMatch(matchID);
                if (match == null) {
                    match = getMatchFromRiot(matchID, account);
                    matches.add(match);
                }
                System.out.println(match);
                account.addMatch(match);
            }
        }

        account.setLastUpdated(Time.getUTC());
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

    public static JsonObject getJsonObject(String jsonString) {
        return gson.fromJson(jsonString, JsonObject.class);
    }
}
