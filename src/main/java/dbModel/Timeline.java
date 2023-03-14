package dbModel;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity
@Table(name = "timelines")
public class Timeline implements Insertable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "timeline")
    private Match match;
}
