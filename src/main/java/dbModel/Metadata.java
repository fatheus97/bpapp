package dbModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "metadatas")
public class Metadata implements Insertable{
    @Id
    private long matchID;
}
