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

import java.util.ArrayList;
import java.util.List;

public class DataExtractor {
    private static final String TOURNAMENT = "LEC/2023+Season/Winter+Season";
    private final Gson gson;

    public DataExtractor() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        this.gson = gsonBuilder.create();
    }

    /**
    fdhh
     @param name asdasd
     @return oralkhsaflkasf
     */
    public Organization getOrganization(String name) {

        Organization organization = new Organization(name, getOrganizationShortcut(name));
        Roster roster = getRoster(name);
        organization.setRoster(roster);

        return organization;
    }

    //gcmnfmj
    private String getOrganizationShortcut(String name) {

        String codedName = Utils.encodeString(name);
        String url = "https://lol.fandom.com/api.php?action=cargoquery&format=json&tables=Teams&fields=Short&where=Teams.Name=%27" + codedName + "%27";
        String jsonString = Utils.getJSONFromURL(url);
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

        return jsonObject.get("cargoquery").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsJsonObject().get("Short").getAsString();
    }

    private Roster getRoster(String name) {

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

    public Organization getOrganizationPlayers(Organization org) {

        List<Player> players = org.getRoster().getPlayers();

        for (Player p : players) {
            List<Account> accounts = getPlayerAccounts(p.getName());
            p.setSoloQAccounts(accounts);
        }

        return org;
    }

    private Player getPlayer(String playerName, String orgName) {

        return new Player(playerName, new Account(getOrganizationShortcut(orgName) + " " + playerName));
    }

    private List<Account> getPlayerAccounts(String playerName) {

        List<Account> accounts = new ArrayList<>();

        String codedName = Utils.encodeString(playerName);
        String url = "https://lolpros.gg/player/" + codedName;
        Document doc = Utils.getDocumentFromURL(url);

        if(doc.getElementsByClass("accounts-list").isEmpty()) {
            accounts.add(getAccount(doc.getElementById("summoner-names").getElementsByTag("p").get(0).text()));
        } else {
            Elements accountElements = doc.getElementsByClass("accounts-list").get(0).getElementsByTag("span");
            for (Element e: accountElements) {
                accounts.add(getAccount(e.text()));
            }
        }

        return accounts;
    }

    private Account getAccount(String name) {

        String codedName = Utils.encodeString(name);
        String url = "https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + codedName;
        String jsonString = Utils.getJSONFromURL(url);

        Account account = gson.fromJson(jsonString, Account.class);

        return account;
    }

    public Organization getOrganizationSoloQMatches(Organization org) {

        org.getRoster().getPlayers().forEach(player -> {
            player.getSoloQAccounts().forEach(account -> {
                account.setMatches(getAccountMatches(account.getPuuid()));
            });
        });

        return org;
    }

    private List<Match> getAccountMatches(String puuid) {

        String codedPuuid = Utils.encodeString(puuid);
        String url = "https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + codedPuuid + "/ids?queue=420startTime=" + Utils.getTimestampInSeconds() + "&count=100";
        String jsonString = Utils.getJSONFromURL(url);
        String[] listOfMatchIDs = gson.fromJson(jsonString, String[].class);

        /*for (String ID: listOfMatchIDs) {
            player.addMatch(this.getMatchByID(ID));
        }

        System.out.println(Arrays.toString(listOfMatchIDs));*/

        return null;
    }

    public void addMatchListToSummoner(Player player) {
/*
        String url = "https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + player.getPuuid() + "/ids?queue=420&start=0&count=10";
        String jsonString = this.getJSONFromURL(url);
        String[] listOfMatchIDs = gson.fromJson(jsonString, String[].class);

        for (String ID: listOfMatchIDs) {
//            player.addMatch(this.getMatchByID(ID));
        }

        System.out.println(Arrays.toString(listOfMatchIDs));*/
    }

    public Match getMatchByID(String ID) {

        String url = "https://europe.api.riotgames.com/lol/match/v5/matches/" + ID;
        String jsonString = Utils.getJSONFromURL(url);

        return gson.fromJson(jsonString, Match.class);
    }


}
