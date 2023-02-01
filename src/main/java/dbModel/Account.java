package dbModel;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String puuid;
    private String name;
    private List<Match> matches;
    private Boolean competitive;

    public Account() {
        matches = new ArrayList<>();
        competitive = false;
    }

    public Account(String name, Boolean competitive) {
        this.name = name;
        this.competitive = competitive;
    }

    public String getName() {
        return name;
    }

    public String getPuuid() {
        return puuid;
    }

    public Boolean getCompetitive() {
        return competitive;
    }

    public void setCompetitive(Boolean competitive) {
        this.competitive = competitive;
    }

    @Override
    public String toString() {
        return "Account{" +
                "puuid='" + puuid + '\'' +
                ", name='" + name + '\'' +
                ", matches=" + matches +
                '}';
    }

    public void addMatch(Match match) {
        matches.add(match);
    }
}
