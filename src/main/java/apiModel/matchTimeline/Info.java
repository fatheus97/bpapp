package apiModel.matchTimeline;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

public class Info {
    private long frameInterval;
    private List<Frame> frames;
    private long gameID;
    private List<Participant> participants;

    @JsonProperty("frameInterval")
    public long getFrameInterval() { return frameInterval; }
    @JsonProperty("frameInterval")
    public void setFrameInterval(long value) { this.frameInterval = value; }

    @JsonProperty("frames")
    public List<Frame> getFrames() { return frames; }
    @JsonProperty("frames")
    public void setFrames(List<Frame> value) { this.frames = value; }

    @JsonProperty("gameId")
    public long getGameID() { return gameID; }
    @JsonProperty("gameId")
    public void setGameID(long value) { this.gameID = value; }

    @JsonProperty("participants")
    public List<Participant> getParticipants() { return participants; }
    @JsonProperty("participants")
    public void setParticipants(List<Participant> value) { this.participants = value; }
}
