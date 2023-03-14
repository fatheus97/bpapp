package dbModel;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity
@Table(name = "infos")
public class Info implements Insertable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "info")
    private Match match;
}
