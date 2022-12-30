package model;

import java.util.ArrayList;
import java.util.List;

public class Summoner {
    private String puuid;
    private String name;
    private List<Match> matches = new ArrayList<>();

    public String getPuuid() {
        return puuid;
    }

    public String getName() {
        return name;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void addMatch(Match match) {
        this.matches.add(match);
    }
}
