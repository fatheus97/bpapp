package apiModel;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

public class Team {
    private List<Ban> bans;
    private Objectives objectives;
    private long teamID;
    private boolean win;

    @JsonProperty("bans")
    public List<Ban> getBans() { return bans; }
    @JsonProperty("bans")
    public void setBans(List<Ban> value) { this.bans = value; }

    @JsonProperty("objectives")
    public Objectives getObjectives() { return objectives; }
    @JsonProperty("objectives")
    public void setObjectives(Objectives value) { this.objectives = value; }

    @JsonProperty("teamId")
    public long getTeamID() { return teamID; }
    @JsonProperty("teamId")
    public void setTeamID(long value) { this.teamID = value; }

    @JsonProperty("win")
    public boolean getWin() { return win; }
    @JsonProperty("win")
    public void setWin(boolean value) { this.win = value; }
}
