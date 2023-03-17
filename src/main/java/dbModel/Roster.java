package dbModel;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rosters")
public class Roster implements Insertable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer nOfChanges;
    @ManyToOne(cascade = CascadeType.MERGE)
    private Organization org;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Player> players = new ArrayList<>();

    public Organization getOrg() {
        return org;
    }

    public void setOrg(Organization org) {
        this.org = org;
    }

    public void setNOfChanges(Integer nOfChanges) {
        this.nOfChanges = nOfChanges;
    }

    @Override
    public String toString() {
        return "Roster{" +
                "id=" + id +
                ", players=" + players +
                '}';
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public List<Player> getPlayers() {
        return players;
    }
}