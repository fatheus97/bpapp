package apiModel.matchData;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

public class Data {
    private long gameCreation;
    private long gameDuration;
    private long gameEndTimestamp;
    private long gameID;
    private String gameMode;
    private String gameName;
    private long gameStartTimestamp;
    private String gameType;
    private String gameVersion;
    private long mapID;
    private List<Participant> participants;
    private String platformID;
    private long queueID;
    private List<Team> teams;
    private String tournamentCode;

    @JsonProperty("gameCreation")
    public long getGameCreation() { return gameCreation; }
    @JsonProperty("gameCreation")
    public void setGameCreation(long value) { this.gameCreation = value; }

    @JsonProperty("gameDuration")
    public long getGameDuration() { return gameDuration; }
    @JsonProperty("gameDuration")
    public void setGameDuration(long value) { this.gameDuration = value; }

    @JsonProperty("gameEndTimestamp")
    public long getGameEndTimestamp() { return gameEndTimestamp; }
    @JsonProperty("gameEndTimestamp")
    public void setGameEndTimestamp(long value) { this.gameEndTimestamp = value; }

    @JsonProperty("gameId")
    public long getGameID() { return gameID; }
    @JsonProperty("gameId")
    public void setGameID(long value) { this.gameID = value; }

    @JsonProperty("gameMode")
    public String getGameMode() { return gameMode; }
    @JsonProperty("gameMode")
    public void setGameMode(String value) { this.gameMode = value; }

    @JsonProperty("gameName")
    public String getGameName() { return gameName; }
    @JsonProperty("gameName")
    public void setGameName(String value) { this.gameName = value; }

    @JsonProperty("gameStartTimestamp")
    public long getGameStartTimestamp() { return gameStartTimestamp; }
    @JsonProperty("gameStartTimestamp")
    public void setGameStartTimestamp(long value) { this.gameStartTimestamp = value; }

    @JsonProperty("gameType")
    public String getGameType() { return gameType; }
    @JsonProperty("gameType")
    public void setGameType(String value) { this.gameType = value; }

    @JsonProperty("gameVersion")
    public String getGameVersion() { return gameVersion; }
    @JsonProperty("gameVersion")
    public void setGameVersion(String value) { this.gameVersion = value; }

    @JsonProperty("mapId")
    public long getMapID() { return mapID; }
    @JsonProperty("mapId")
    public void setMapID(long value) { this.mapID = value; }

    @JsonProperty("participants")
    public List<Participant> getParticipants() { return participants; }
    @JsonProperty("participants")
    public void setParticipants(List<Participant> value) { this.participants = value; }

    @JsonProperty("platformId")
    public String getPlatformID() { return platformID; }
    @JsonProperty("platformId")
    public void setPlatformID(String value) { this.platformID = value; }

    @JsonProperty("queueId")
    public long getQueueID() { return queueID; }
    @JsonProperty("queueId")
    public void setQueueID(long value) { this.queueID = value; }

    @JsonProperty("teams")
    public List<Team> getTeams() { return teams; }
    @JsonProperty("teams")
    public void setTeams(List<Team> value) { this.teams = value; }

    @JsonProperty("tournamentCode")
    public String getTournamentCode() { return tournamentCode; }
    @JsonProperty("tournamentCode")
    public void setTournamentCode(String value) { this.tournamentCode = value; }
}
