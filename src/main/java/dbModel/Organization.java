package dbModel;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "organizations")
public class Organization implements Showable, Insertable {
    @Id
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
    public String[] getColumnNames() {
        return new String[]{"Role", "Name"};
    }

    @Override
    public String[][] getContent() {
        List<Player> players = getRoster().getPlayers();
        players.sort(Comparator.comparing(Player::getRole));
        String[][] content = new String[players.size()][2];;
        for (int i = 0; i < players.size(); i++) {
            content[i][0] = players.get(i).getRole().name();
            content[i][1] = players.get(i).getName();
        }

        return content;
    }
}
