package dbModel;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "players")
public class Player implements Showable, Insertable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NaturalId
    private String name;
    @OneToOne
    private Roster roster;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "player")
    private List<Account> accounts = new ArrayList<>();

    public Player(String name, Roster roster) {
        this.name = name;
        this.roster = roster;
    }

    public Player() {

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
                ", Accounts=" + accounts +
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
}
