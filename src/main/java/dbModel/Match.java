package dbModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matches")
public class Match implements Insertable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany(mappedBy = "matches")
    private List<Account> accounts = new ArrayList<>();
    @OneToOne(cascade = CascadeType.ALL)
    private Metadata metadata;
    @OneToOne(cascade = CascadeType.ALL)
    private Info info;
    @OneToOne(cascade = CascadeType.ALL)
    private Timeline timeline;


    public Match(Metadata metadata, Info info, Timeline timeline) {
        this.metadata = metadata;
        this.info = info;
        this.timeline = timeline;
    }

    public Match(Info info, Timeline timeline, Account participant) {
        this.info = info;
        this.timeline = timeline;
        this.accounts.add(participant);
    }

    public Match() {

    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", accounts=" + accounts +
                ", metadata=" + metadata +
                ", info=" + info +
                ", timeline=" + timeline +
                '}';
    }
}
