package dbModel;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity
@Table(name = "metadatas")
public class Metadata implements Insertable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "metadata")
    private Match match;
}
