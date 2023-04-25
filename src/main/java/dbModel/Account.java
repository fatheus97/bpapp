package dbModel;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account implements Insertable {
    @Id
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
    private LocalDateTime lastUpdated;

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
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

    public void addMatch(Match match) {
        matches.add(match);
    }
}
