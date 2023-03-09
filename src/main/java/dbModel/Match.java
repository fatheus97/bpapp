package dbModel;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany(mappedBy = "matches")
    private List<Account> participants;
    @OneToOne(cascade = CascadeType.ALL)
    private Metadata metadata; // TODO: 09.03.2023 metadata dataModel 
    @OneToOne(cascade = CascadeType.ALL)
    private Data data;
    @OneToOne(cascade = CascadeType.ALL)
    private Timeline timeline;


    public Match(Metadata metadata, Data data, Timeline timeline) {
        this.metadata = metadata;
        this.data = data;
        this.timeline = timeline;
    }

    public Match(Data data, Timeline timeline) {
        this.data = data;
        this.timeline = timeline;
    }

    public Match() {

    }

    @Override
    public String toString() {
        return "Match{" +
                "metadata=" + metadata +
                ", data=" + data +
                ", timeline=" + timeline +
                '}';
    }
}
