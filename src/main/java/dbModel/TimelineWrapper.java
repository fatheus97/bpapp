package dbModel;

import com.fasterxml.jackson.annotation.*;
import com.google.gson.annotations.SerializedName;

public class TimelineWrapper {
    @SerializedName("info")
    private Timeline timeline;

    @JsonProperty("info")
    public Timeline getTimeline() { return timeline; }
    @JsonProperty("info")
    public void setTimeline(Timeline value) { this.timeline = value; }
}
