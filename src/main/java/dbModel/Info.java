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
    @OneToMany(mappedBy = "info", cascade = CascadeType.ALL)
    private List<Participant> participants = new ArrayList<>();

    public List<Participant> getParticipants() {
        return participants;
    }

    @JsonProperty("gameId")
    public long getGameID() { return gameID; }
    @JsonProperty("gameId")
    public void setGameID(long value) { this.gameID = value; }

    @Override
    public String toString() {
        return "Info{" +
                "gameID=" + gameID +
                '}';
    }
}
