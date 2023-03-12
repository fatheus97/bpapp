package dbModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "accounts")
public class Account implements Insertable {
    @Id
    private String name;
    private String puuid;
    @ManyToOne
    private Player player;
    @ManyToMany(mappedBy = "participants")
    private List<Match> matches = new ArrayList<>();
    private Boolean competitive;

    public Account() {
        competitive = false;
    }

    public Account(String name, Boolean competitive, Player player) {
        this.name = name;
        this.competitive = competitive;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
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
