package apiModel;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

public class Metadata {
    private String dataVersion;
    private String matchID;
    private List<String> participants;

    @JsonProperty("dataVersion")
    public String getDataVersion() { return dataVersion; }
    @JsonProperty("dataVersion")
    public void setDataVersion(String value) { this.dataVersion = value; }

    @JsonProperty("matchId")
    public String getMatchID() { return matchID; }
    @JsonProperty("matchId")
    public void setMatchID(String value) { this.matchID = value; }

    @JsonProperty("participants")
    public List<String> getParticipants() { return participants; }
    @JsonProperty("participants")
    public void setParticipants(List<String> value) { this.participants = value; }
}
