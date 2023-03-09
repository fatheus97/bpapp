package dbModel;

import java.util.ArrayList;
import java.util.List;

// TODO: 09.03.2023 hibernate annotation 
public class Organization implements Showable {
    private String name;
    private String shortcut;
    private Roster roster;

    public Organization(String name, String shortcut) {
        this.name = name;
        this.shortcut = shortcut;
    }

    public String getShortcut() {
        return shortcut;
    }

    public String getName() {
        return name;
    }

    public Roster getRoster() {
        return roster;
    }

    public void setRoster(Roster roster) {
        this.roster = roster;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "name='" + name + '\'' +
                ", shortcut='" + shortcut + '\'' +
                ", roster=" + roster +
                '}';
    }

    @Override
    public String getHeader() {
        return name;
    }

    @Override
    public List<String> getContent() {
        List<String> content = new ArrayList<>();
        List<Player> players = roster.getPlayers();
        for (Player p : players) {
            content.add(p.getName());
        }

        return content;
    }
}
