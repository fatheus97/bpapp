package dbModel;

import java.util.ArrayList;
import java.util.List;

public class Player implements Showable{
    private String name;
    private List<Account> accounts = new ArrayList<>();

    public Player(String name) {
        this.name = name;
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
