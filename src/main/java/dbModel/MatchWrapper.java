package dbModel;

import com.fasterxml.jackson.annotation.*;

public class MatchWrapper {
    private Info info;

    @JsonProperty("info")
    public Info getInfo() { return info; }
    @JsonProperty("info")
    public void setInfo(Info value) { this.info = value; }
}
