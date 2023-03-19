package dbModel;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "players")
public class Player implements Showable, Insertable {
    @Id
    private String name;
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToMany(mappedBy = "players")
    private List<Roster> rosters = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "player")
    private List<Account> accounts = new ArrayList<>();

    public Player(String name, Role role, Roster roster) {
        this.name = name;
        this.role = role;
        this.rosters.add(roster);
    }

    public Player() {

    }

    public Role getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account acc) {
        accounts.add(acc);
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", role=" + role +
                ", accounts=" + accounts +
                '}';
    }

    @Override
    public String getHeader() {
        return name;
    }

    @Override
    public List<String> getContent() {
        List<String> content = new ArrayList<>();
        for (Account a : accounts) {
            content.add(a.getName());
        }
        return content;
    }

    public void deleteAccount(String puuid) {
        accounts.removeIf(account -> account.getPuuid().equals(puuid));
    }
}
