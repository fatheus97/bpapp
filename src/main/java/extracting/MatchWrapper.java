package extracting;

import com.fasterxml.jackson.annotation.*;
import dbModel.Info;

public class MatchWrapper {
    private Info info;

    @JsonProperty("info")
    public Info getInfo() {
        return info;
    }

    @JsonProperty("info")
    public void setInfo(Info value) {
        this.info = value;
    }
}
