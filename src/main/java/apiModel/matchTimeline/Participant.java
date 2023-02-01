package apiModel.matchTimeline;

import com.fasterxml.jackson.annotation.*;

public class Participant {
    private long participantID;
    private String puuid;

    @JsonProperty("participantId")
    public long getParticipantID() { return participantID; }
    @JsonProperty("participantId")
    public void setParticipantID(long value) { this.participantID = value; }

    @JsonProperty("puuid")
    public String getPuuid() { return puuid; }
    @JsonProperty("puuid")
    public void setPuuid(String value) { this.puuid = value; }
}
