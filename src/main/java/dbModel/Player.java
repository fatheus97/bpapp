package dbModel;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "players")
public class Player implements Insertable {
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

    public void deleteAccount(String puuid) {
        accounts.removeIf(account -> account.getPuuid().equals(puuid));
    }
}
