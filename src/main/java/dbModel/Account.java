package dbModel;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "accounts")
public class Account implements Insertable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NaturalId
    private String name;
    @NaturalId
    private String puuid;
    @ManyToOne
    private Player player;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "accounts_matches",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "match_id"))
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", puuid='" + puuid + '\'' +
                ", player=" + player +
                ", matches=" + matches +
                ", competitive=" + competitive +
                '}';
    }

    public void addMatch(Match match) {
        matches.add(match);
    }
}
