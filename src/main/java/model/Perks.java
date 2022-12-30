package model;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

public class Perks {
    private StatPerks statPerks;
    private List<Style> styles;

    @JsonProperty("statPerks")
    public StatPerks getStatPerks() { return statPerks; }
    @JsonProperty("statPerks")
    public void setStatPerks(StatPerks value) { this.statPerks = value; }

    @JsonProperty("styles")
    public List<Style> getStyles() { return styles; }
    @JsonProperty("styles")
    public void setStyles(List<Style> value) { this.styles = value; }
}
