package dbModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "timelines")
public class Timeline {
    @Id
    private long gameID;
}
