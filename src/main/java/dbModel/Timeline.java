package dbModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@Entity
@Table(name = "timelines")
public class Timeline implements Insertable {
    @Id
    @SerializedName("gameId")
    private long gameID;
    private long frameInterval;
    @OneToOne(mappedBy = "timeline")
    private Match match;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<Frame> frames;

    @JsonProperty("gameId")
    public long getGameID() { return gameID; }
    @JsonProperty("gameId")
    public void setGameID(long value) { this.gameID = value; }

    @JsonProperty("frameInterval")
    public long getFrameInterval() { return frameInterval; }
    @JsonProperty("frameInterval")
    public void setFrameInterval(long value) { this.frameInterval = value; }

    @JsonProperty("frames")
    public List<Frame> getFrames() { return frames; }
    @JsonProperty("frames")
    public void setFrames(List<Frame> value) { this.frames = value; }

    @Override
    public String toString() {
        return "Timeline{" +
                "gameID=" + gameID +
                ", frameInterval=" + frameInterval +
                ", frames=" + frames.size() +
                '}';
    }
}
