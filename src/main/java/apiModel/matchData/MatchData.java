package apiModel.matchData;

import com.fasterxml.jackson.annotation.*;
import com.google.gson.annotations.SerializedName;

public class MatchData {
    private Metadata metadata;
    @SerializedName("info")
    private Data data;

    @JsonProperty("metadata")
    public Metadata getMetadata() { return metadata; }
    @JsonProperty("metadata")
    public void setMetadata(Metadata value) { this.metadata = value; }

    @JsonProperty("info")
    public Data getData() { return data; }
    @JsonProperty("info")
    public void setData(Data value) { this.data = value; }
}
