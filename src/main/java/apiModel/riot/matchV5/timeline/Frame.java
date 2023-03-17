package apiModel.riot.matchV5.timeline;

import com.fasterxml.jackson.annotation.*;
import java.util.List;
import java.util.Map;

public class Frame {
    private List<Event> events;
    private Map<String, ParticipantFrame> participantFrames;
    private long timestamp;

    @JsonProperty("events")
    public List<Event> getEvents() { return events; }
    @JsonProperty("events")
    public void setEvents(List<Event> value) { this.events = value; }

    @JsonProperty("participantFrames")
    public Map<String, ParticipantFrame> getParticipantFrames() { return participantFrames; }
    @JsonProperty("participantFrames")
    public void setParticipantFrames(Map<String, ParticipantFrame> value) { this.participantFrames = value; }

    @JsonProperty("timestamp")
    public long getTimestamp() { return timestamp; }
    @JsonProperty("timestamp")
    public void setTimestamp(long value) { this.timestamp = value; }
}
