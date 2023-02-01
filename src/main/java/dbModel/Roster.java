package dbModel;

import java.util.ArrayList;
import java.util.List;

public class Roster {
    private Player top;
    private Player jungle;
    private Player mid;
    private Player bot;
    private Player support;

    public Player getTop() {
        return top;
    }

    public Player getJungle() {
        return jungle;
    }

    public Player getMid() {
        return mid;
    }

    public Player getBot() {
        return bot;
    }

    public Player getSupport() {
        return support;
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(getTop());
        players.add(getJungle());
        players.add(getMid());
        players.add(getBot());
        players.add(getSupport());

        return players;
    }

    public void setTop(Player top) {
        this.top = top;
    }

    public void setJungle(Player jungle) {
        this.jungle = jungle;
    }

    public void setMid(Player mid) {
        this.mid = mid;
    }

    public void setBot(Player bot) {
        this.bot = bot;
    }

    public void setSupport(Player support) {
        this.support = support;
    }

    @Override
    public String toString() {
        return "Roster{" + "\n" +
                "top=" + top + ",\n" +
                "jungle=" + jungle + ",\n" +
                "mid=" + mid + ",\n" +
                "bot=" + bot + ",\n" +
                "support=" + support + "\n" +
                '}';
    }
}