package dbModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "infos")
public class Info implements Insertable{
    @Id
    private long gameID;
}
