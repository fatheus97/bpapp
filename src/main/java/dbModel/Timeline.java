package dbModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.*;

@Entity
@Table(name = "timelines")
public class Timeline implements Insertable {
    @Id
    @SerializedName("gameId")
    private long gameID;
    @OneToOne(mappedBy = "timeline")
    private Match match;

    @JsonProperty("gameId")
    public long getGameID() { return gameID; }
    @JsonProperty("gameId")
    public void setGameID(long value) { this.gameID = value; }

    @Override
    public String toString() {
        return "Timeline{" +
                "gameID=" + gameID +
                '}';
    }
}
