package apiModel.riot.matchV5.timeline;

import com.fasterxml.jackson.annotation.*;

public class MatchData {
    private Metadata metadata;
    private Info info;

    @JsonProperty("metadata")
    public Metadata getMetadata() { return metadata; }
    @JsonProperty("metadata")
    public void setMetadata(Metadata value) { this.metadata = value; }

    @JsonProperty("info")
    public Info getInfo() { return info; }
    @JsonProperty("info")
    public void setInfo(Info value) { this.info = value; }
}
