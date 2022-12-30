package model;

import com.fasterxml.jackson.annotation.*;

public class Ban {
    private long championID;
    private long pickTurn;

    @JsonProperty("championId")
    public long getChampionID() { return championID; }
    @JsonProperty("championId")
    public void setChampionID(long value) { this.championID = value; }

    @JsonProperty("pickTurn")
    public long getPickTurn() { return pickTurn; }
    @JsonProperty("pickTurn")
    public void setPickTurn(long value) { this.pickTurn = value; }
}
