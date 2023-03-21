package dbModel;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matches")
public class Match implements Insertable {
    @Id
    private String matchID;
    @ManyToMany(mappedBy = "matches")
    private List<Account> accounts = new ArrayList<>();
    @OneToOne(cascade = CascadeType.ALL)
    private Info info;
    @OneToOne(cascade = CascadeType.ALL)
    private Timeline timeline;

    public Match(String matchID, Info info, Timeline timeline, Account participant) {
        this.matchID = matchID;
        this.info = info;
        this.timeline = timeline;
        this.accounts.add(participant);
    }

    public Match() {

    }

    public Info getInfo() {
        return info;
    }

    public String getMatchID() {
        return matchID;
    }

    @Override
    public String toString() {
        return "Match{" +
                "matchID='" + matchID + '\'' +
                ", info=" + info +
                ", timeline=" + timeline +
                '}';
    }
}
