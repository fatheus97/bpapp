package dbModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import org.hibernate.annotations.NaturalId;

import jakarta.persistence.*;

@Entity
@Table(name = "infos")
public class Info implements Insertable {
    @Id
    @SerializedName("gameId")
    private long gameID;
    @OneToOne(mappedBy = "info")
    private Match match;

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
