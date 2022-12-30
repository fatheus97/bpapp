package model;

import com.fasterxml.jackson.annotation.*;

public class StatPerks {
    private long defense;
    private long flex;
    private long offense;

    @JsonProperty("defense")
    public long getDefense() { return defense; }
    @JsonProperty("defense")
    public void setDefense(long value) { this.defense = value; }

    @JsonProperty("flex")
    public long getFlex() { return flex; }
    @JsonProperty("flex")
    public void setFlex(long value) { this.flex = value; }

    @JsonProperty("offense")
    public long getOffense() { return offense; }
    @JsonProperty("offense")
    public void setOffense(long value) { this.offense = value; }
}
