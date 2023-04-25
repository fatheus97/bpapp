package dbModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private long teamId;
    private boolean win;
    @ManyToOne
    private Info info;

    public void setInfo(Info info) {
        this.info = info;
    }

    @JsonProperty("teamId")
    public long getTeamId() {
        return teamId;
    }

    @JsonProperty("teamId")
    public void setTeamId(long value) {
        this.teamId = value;
    }

    @JsonProperty("win")
    public boolean getWin() {
        return win;
    }

    @JsonProperty("win")
    public void setWin(boolean value) {
        this.win = value;
    }
}
