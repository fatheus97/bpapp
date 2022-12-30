import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

public class DataExtractor {

    private static final String APIKEY = "RGAPI-9700535a-e366-4a8e-858d-e2ab8f2889e2";
    private GsonBuilder gsonBuilder = new GsonBuilder();
    private Gson gson = gsonBuilder.create();

    public static void main(String[] args) throws IOException {
        DataExtractor dataExtractor = new DataExtractor();
        Summoner summoner = dataExtractor.getSummonerByName("ALL RigiCZ");

        dataExtractor.addMatchListToSummoner(summoner);
    }

    public Summoner getSummonerByName(String name) throws IOException {

        String codedName = new String(URLEncoder.encode(name, "UTF-8"));
        String url = "https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + codedName;
        String jsonString = this.getDataFromURL(url);

        return gson.fromJson(jsonString, Summoner.class);
    }

    public void addMatchListToSummoner(Summoner summoner) throws IOException {

        String url = "https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + summoner.getPuuid() + "/ids?queue=420&start=0&count=10";
        String jsonString = this.getDataFromURL(url);
        String[] listOfMatchIDs = gson.fromJson(jsonString, String[].class);

        for (String ID: listOfMatchIDs) {
            summoner.addMatch(this.getMatchByID(ID));
        }

        System.out.println(Arrays.toString(listOfMatchIDs));
    }

    public Match getMatchByID(String ID) throws IOException {

        String url = "https://europe.api.riotgames.com/lol/match/v5/matches/" + ID;
        String jsonString = this.getDataFromURL(url);

        return gson.fromJson(jsonString, Match.class);
    }

    public String getDataFromURL(String url) throws IOException {
        URL url1 = new URL(url);//your url i.e fetch data from .
        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        conn.setRequestProperty("Accept-Language", "cs-CZ,cs;q=0.9,en;q=0.8");
        conn.setRequestProperty("Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8");
        conn.setRequestProperty("Origin", "https://developer.riotgames.com");
        conn.setRequestProperty("X-Riot-Token", APIKEY);
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP Error code : "
                    + conn.getResponseCode());
        }
        java.util.Scanner s = new java.util.Scanner(conn.getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
