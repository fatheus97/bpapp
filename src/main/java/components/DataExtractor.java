package components;

import dbModel.*;
import com.google.gson.*;
import errorHandling.PlayerNotFoundInLoLProsException;
import extracting.MatchWrapper;
import extracting.TimelineWrapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DataExtractor {
    private static final Properties props = new Properties();
    static {
        try {
            props.load(NetworkUtil.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String tournament;
    private static final int START_DAYS = Integer.parseInt(props.getProperty("start.days"));
    private static final int END_DAYS = Integer.parseInt(props.getProperty("end.days"));
    private static final String LOLFANDOM_API = props.getProperty("lolfandom.api");
    private static final String LOLFANDOM_WIKI = props.getProperty("lolfandom.wiki");
    private static final String LOLPROS = props.getProperty("lolpros");
    private static final String API_RIOT_EUW = props.getProperty("api.riot.euw");
    private static final String API_RIOT_EU = props.getProperty("api.riot.eu");
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

    private DataExtractor() {

    }

    public static void setTournament(String tournament) {
        DataExtractor.tournament = tournament;
    }

    public static Organisation getOrganisation(String name) throws URISyntaxException, IOException, InterruptedException {

        Organisation org = DatabaseManager.getObject(Organisation.class, name);

        if (org != null){
            updateRoster(org);
        } else {
            org = new Organisation(name, getOrganisationShortcut(name));
            org.addRoster(getRoster(org));
        }

        return org;
    }

    private static String getOrganisationShortcut(String name) throws URISyntaxException, IOException, InterruptedException {

        String urlString = LOLFANDOM_API + "?action=cargoquery" +
                "&format=json" +
                "&tables=Teams" +
                "&fields=Short" +
                "&where=Teams.OverviewPage='" + name + "'";
        String jsonString = NetworkUtil.getJSONFromURLString(urlString);
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

        return jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("Short").getAsString();
    }

    private static void updateRoster(Organisation org) throws URISyntaxException, IOException, InterruptedException {

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

    private static Roster getRoster(Organisation org) throws URISyntaxException, IOException, InterruptedException {

        String urlString = LOLFANDOM_API + "?action=cargoquery" +
                "&format=json" +
                "&tables=TournamentRosters" +
                "&fields=RosterLinks,Roles" +
                "&where=TournamentRosters.Team='" + org.getName() +
                "' AND TournamentRosters.OverviewPage='" + tournament + "'";

        String jsonString = NetworkUtil.getJSONFromURLString(urlString);
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

        String playersNameString = jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("RosterLinks").getAsString();
        String playersRoleString = jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("Roles").getAsString();


        String[] playersNameArray = playersNameString.split(";;");
        String[] playersRoleArray = playersRoleString.split(";;");

        Roster roster = new Roster();
        roster.setOrg(org);

        for (int i = 0; i < playersNameArray.length; i++) {
            if (!playersRoleArray[i].equalsIgnoreCase("coach")) {
                Role role = Role.valueOf(playersRoleArray[i].toUpperCase());
                String name = playersNameArray[i];
                roster.addPlayer(new Player(name, role , roster));
            }
        }

        return roster;
    }

    public static void fetchAccountsToPlayer(Player player) throws IOException, URISyntaxException, InterruptedException, PlayerNotFoundInLoLProsException {

        List<Account> newAccounts= getAccounts(player);
        Set<String> oldAccountsPUUIDs = player.getAccounts().stream()
                .map(Account::getPuuid)
                .collect(Collectors.toSet());
        Set<String> newAccountsPUUIDs = newAccounts.stream()
                .map(Account::getPuuid)
                .collect(Collectors.toSet());

        oldAccountsPUUIDs.stream()
                .filter(puuid -> !newAccountsPUUIDs.contains(puuid))
                .forEach(player::deleteAccount);

        newAccounts.stream()
                .filter(account -> !oldAccountsPUUIDs.contains(account.getPuuid()))
                .forEach(player::addAccount);
    }

    private static List<Account> getAccounts(Player player) throws URISyntaxException, IOException, InterruptedException, PlayerNotFoundInLoLProsException {
        String urlString = LOLFANDOM_API + "?action=cargoquery" +
                "&format=json" +
                "&tables=Players" +
                "&fields=Lolpros" +
                "&where=OverviewPage='" + player.getName() + "'";

        String jsonString = NetworkUtil.getJSONFromURLString(urlString);
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        String lolProsURL = jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("Lolpros").getAsString();

        Document doc;
        try {
            doc = NetworkUtil.getDocumentFromURLString(lolProsURL);
        } catch (IOException e) {
            try {
                doc = NetworkUtil.getDocumentFromURLString(LOLPROS + "/player/" + player.getName());
            } catch (IOException ex) {
                throw new PlayerNotFoundInLoLProsException(player.getName());
            }
        }

        List<Account> accounts = new ArrayList<>();
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

        String jsonString = NetworkUtil.getJSONFromURLString(API_RIOT_EUW + "/lol/summoner/v4/summoners/by-name/" + name);
        Account account = gson.fromJson(jsonString, Account.class);
        account.setPlayer(player);
        return account;
    }

    public static void fetchMatchesToRoster(Roster roster) throws URISyntaxException, IOException, InterruptedException {
        matches.addAll(roster.getMatches());

        String lastUpdateTime = "0";
        if(roster.getLastUpdated() != null){
            lastUpdateTime = roster.getLastUpdated().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss"));
        }
        String startTime = TimeUtil.getUTCString(START_DAYS, DateTimeFormatter.ofPattern("yyyyMMddhhmmss"));
        String endTime = TimeUtil.getUTCString(END_DAYS, DateTimeFormatter.ofPattern("yyyyMMddhhmmss"));

        String urlString = LOLFANDOM_API + "?action=cargoquery" +
                "&format=json" +
                "&tables=ScoreboardTeams=ST,ScoreboardGames=SG,PostgameJsonMetadata=PJM" +
                "&join_on=ST.GameId=SG.GameId,SG.RiotPlatformGameId=PJM.RiotPlatformGameId" +
                "&fields=PJM.StatsPage" +
                "&where=ST.Team='" + roster.getOrg().getName() +
                "' AND ST.Roster__full='" + roster.playersToString() +
                "' AND SG.DateTime_UTC>" + lastUpdateTime +
                " AND SG.DateTime_UTC>" + startTime +
                " AND SG.DateTime_UTC<" + endTime;

        String jsonString = NetworkUtil.getJSONFromURLString(urlString);
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        if (jsonObject.get("cargoquery") != null) {
            jsonObject.get("cargoquery").getAsJsonArray().forEach(jsonElement -> {
                String matchID = jsonElement.getAsJsonObject()
                        .get("title").getAsJsonObject()
                        .get("StatsPage").getAsString();
                Match match = returnMatch(matchID);
                if (match == null) {
                    try {
                        match = getMatchFromLeaguepedia(matchID, roster);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    matches.add(match);
                }
                roster.addMatch(match);
            });
        }
        roster.setLastUpdated(TimeUtil.getUTC());
    }

    private static Match getMatchFromLeaguepedia(String id, Roster roster) throws IOException {
        Document documentInfo = NetworkUtil.getDocumentFromURLString(LOLFANDOM_WIKI + id + "?action=edit");
        String jsonStringInfo = Objects.requireNonNull(documentInfo.getElementById("wpTextbox1")).text();
        Info info = gson.fromJson(jsonStringInfo, Info.class);
        for (Participant participant : info.getParticipants()) {
            participant.setInfo(info);
        }

        Document documentTimeline = NetworkUtil.getDocumentFromURLString(LOLFANDOM_WIKI + id + "/Timeline?action=edit");
        String jsonStringTimeline = Objects.requireNonNull(documentTimeline.getElementById("wpTextbox1")).text();
        Timeline timeline = gson.fromJson(jsonStringTimeline, Timeline.class);

        return new Match(id, info, timeline, roster);
    }
    public static void fetchMatchesToAccount(Account account, int start) throws URISyntaxException, IOException, InterruptedException {
        matches.addAll(account.getMatches());

        long lastUpdateTime = TimeUtil.getEpochInSeconds(START_DAYS);
        if(account.getLastUpdated() != null) {
            lastUpdateTime = account.getLastUpdated().toInstant(ZoneOffset.UTC).toEpochMilli()/1000;
        }

        String urlString = API_RIOT_EU + "/lol/match/v5/matches/by-puuid/" + account.getPuuid() +
                "/ids?startTime=" + lastUpdateTime +
                "&endTime=" + TimeUtil.getEpochInSeconds(END_DAYS) +
                "&queue=420" +
                "&start=" + start +
                "&count=100";

        String jsonString = NetworkUtil.getJSONFromURLString(urlString);

        String[] listOfMatchIDs = gson.fromJson(jsonString, String[].class);

        for (String matchID: listOfMatchIDs) {
            Match match = returnMatch(matchID);
            if (match == null) {
                match = getMatchFromRiot(matchID, account);
                matches.add(match);
                account.addMatch(match);
            }
        }

        if(listOfMatchIDs.length >= 100) {
            fetchMatchesToAccount(account, start + 100);
        }

        account.setLastUpdated(TimeUtil.getUTC());
    }

    private static Match returnMatch(String matchId) {
        for (Match match : matches) {
            if (match.getMatchID().equals(matchId)) {
                return match;
            }
        }
        return null;
    }

    private static Match getMatchFromRiot(String id, Account account) throws URISyntaxException, IOException, InterruptedException {

        String jsonStringData = NetworkUtil.getJSONFromURLString(API_RIOT_EU + "/lol/match/v5/matches/" + id);
        MatchWrapper matchWrapper = gson.fromJson(jsonStringData, MatchWrapper.class);

        Info info = matchWrapper.getInfo();
        for (Participant participant : info.getParticipants()) {
            participant.setInfo(info);
        }

        String jsonStringTimeline = NetworkUtil.getJSONFromURLString(API_RIOT_EU + "/lol/match/v5/matches/" + id + "/timeline");
        TimelineWrapper timelineWrapper = gson.fromJson(jsonStringTimeline, TimelineWrapper.class);

        return new Match(id, info, timelineWrapper.getTimeline(), account);
    }

    private static JsonObject getJsonObject(String jsonString) {
        return gson.fromJson(jsonString, JsonObject.class);
    }

    public static JsonObject getTournamentsByRegion(String region) throws URISyntaxException, IOException, InterruptedException {

        String urlString = LOLFANDOM_API + "?action=cargoquery" +
                "&format=json" +
                "&tables=Tournaments" +
                "&fields=OverviewPage" +
                "&where=Region='" + region + "'" +
                " AND DateStart IS NOT NULL" +
                " AND DateStart >= " + TimeUtil.getUTCString(365, DateTimeFormatter.ofPattern("yyyyMMddhhmmss")) +
                "&order_by=DateStart DESC";

        String jsonString = NetworkUtil.getJSONFromURLString(urlString);

        return getJsonObject(jsonString);
    }

    public static JsonObject getTeamsByTournament(String tournament) throws URISyntaxException, IOException, InterruptedException {
        String urlString = LOLFANDOM_API + "?action=cargoquery" +
                "&format=json" +
                "&tables=TournamentRosters" +
                "&fields=Team" +
                "&where=TournamentRosters.OverviewPage='" + tournament + "'" +
                "&order_by=Team";

        String jsonString = NetworkUtil.getJSONFromURLString(urlString);

        return getJsonObject(jsonString);
    }
}
