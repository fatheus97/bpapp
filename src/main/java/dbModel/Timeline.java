package dbModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "timelines")
public class Timeline implements Insertable {
    @Id
    private long gameID;
}
