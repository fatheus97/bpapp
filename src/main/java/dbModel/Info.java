package dbModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "infos")
public class Info implements Insertable {
    @Id
    @SerializedName("gameId")
    private long gameID;
    @OneToOne(mappedBy = "info")
    private Match match;
    private long gameCreation;
    @OneToMany(mappedBy = "info", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Participant> participants = new ArrayList<>();
    @OneToMany(mappedBy = "info", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Team> teams;

    public List<Participant> getParticipants() {
        return participants;
    }

    @JsonProperty("gameId")
    public long getGameID() { return gameID; }
    @JsonProperty("gameId")
    public void setGameID(long value) { this.gameID = value; }

    @JsonProperty("teams")
    public List<Team> getTeams() { return teams; }
    @JsonProperty("teams")
    public void setTeams(List<Team> value) { this.teams = value; }

    @Override
    public String toString() {
        return "Info{" +
                "gameID=" + gameID +
                ", gameCreation=" + gameCreation +
                ", participants=" + participants +
                '}';
    }
}
