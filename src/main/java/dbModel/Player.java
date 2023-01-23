package dbModel;

import java.util.ArrayList;
import java.util.List;

public class Player implements Showable{
    private String name;
    private Account tournamentAccount;
    private List<Account> soloQAccounts = new ArrayList<>();

    public Player(String name, Account tournamentAccount) {
        this.name = name;
        this.tournamentAccount = tournamentAccount;
    }

    public String getName() {
        return name;
    }

    public void setSoloQAccounts(List<Account> soloQAccounts) {
        this.soloQAccounts = soloQAccounts;
    }

    public List<Account> getSoloQAccounts() {
        return soloQAccounts;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", tournamentAccount=" + tournamentAccount +
                ", soloQAccounts=" + soloQAccounts +
                '}';
    }

    @Override
    public String getHeader() {
        return name;
    }

    @Override
    public List<String> getContent() {
        List<String> content = new ArrayList<>();
        for (Account a : soloQAccounts) {
            content.add(a.getName());
        }
        return content;
    }
}
