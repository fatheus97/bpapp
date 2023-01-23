package dbModel;

import apiModel.Match;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String puuid;
    private String name;
    private List<Match> matches = new ArrayList<>();

    public Account(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPuuid() {
        return puuid;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    @Override
    public String toString() {
        return "Account{" +
                "puuid='" + puuid + '\'' +
                ", name='" + name + '\'' +
                ", matches=" + matches +
                '}';
    }
}
