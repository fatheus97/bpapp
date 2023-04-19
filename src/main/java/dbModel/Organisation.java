package dbModel;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "organizations")
public class Organisation implements Insertable {
    @Id
    private String name;
    @NaturalId
    private String shortcut;
    @OneToMany(mappedBy = "org", cascade = CascadeType.ALL)
    private List<Roster> rosters = new ArrayList<>();
    @Transient
    private Roster startingLineUp;

    public Organisation(String name, String shortcut) {
        this.name = name;
        this.shortcut = shortcut;
    }

    public Organisation() {
    }

    public Roster getStartingLineUp() {
        return startingLineUp;
    }

    public List<Roster> getRosters() {
        return rosters;
    }

    public void setStartingLineUp(Roster startingLineUp) {
        this.startingLineUp = startingLineUp;
    }

    public Roster getLastRoster() {
        return rosters.get(0);
    }

    public Roster getRoster(int i) {
        return rosters.get(i);
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
                ", rosters=" + rosters +
                ", startingLineUp=" + startingLineUp +
                '}';
    }
}
