package dbModel;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "rosters")
public class Roster implements Insertable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer nOfChanges; //for future development
    @ManyToOne(cascade = CascadeType.MERGE)
    private Organisation org;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Player> players = new ArrayList<>();
    @OneToMany(mappedBy = "roster", cascade = CascadeType.ALL)
    private List<Match> matches = new ArrayList<>();
    private LocalDateTime lastUpdated;

    public Roster() {
    }

    public Roster(Organisation org, List<Player> players) {
        this.org = org;
        this.players = players;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Organisation getOrg() {
        return org;
    }

    public Roster setOrg(Organisation org) {
        this.org = org;
        return this;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public String playersToString() {
        return players.stream().map(Player::getName).collect(Collectors.joining(","));
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addMatch(Match match) {
        matches.add(match);
    }
}