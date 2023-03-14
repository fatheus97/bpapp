package dbModel;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Entity
@Table(name = "organizations")
public class Organization implements Showable, Insertable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NaturalId
    private String name;
    @NaturalId
    private String shortcut;
    @OneToMany(mappedBy = "org", cascade = CascadeType.ALL)
    private List<Roster> rosters = new ArrayList<>();

    public Organization(String name, String shortcut) {
        this.name = name;
        this.shortcut = shortcut;
    }

    public Organization() {
    }

    public Roster getRoster() {
        return rosters.get(0);
    }

    public void addRoster(Roster roster) {
        rosters.add(0, roster);
    }

    public String getShortcut() {
        return shortcut;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "name='" + name + '\'' +
                ", shortcut='" + shortcut + '\'' +
                ", rosters=" + rosters +
                '}';
    }

    @Override
    public String getHeader() {
        return name;
    }

    @Override
    public List<String> getContent() {
        List<String> content = new ArrayList<>();
        List<Player> players = rosters.get(0).getPlayers();
        for (Player p : players) {
            content.add(p.getName());
        }

        return content;
    }
}
