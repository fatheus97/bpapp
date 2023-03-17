package apiModel.riot.matchV5.timeline;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

public class Event {
    private Long realTimestamp;
    private long timestamp;
    private EventType type;
    private Long itemID;
    private Long participantID;
    private LevelUpType levelUpType;
    private Long skillSlot;
    private Long creatorID;
    private WardType wardType;
    private Long level;
    private Long bounty;
    private Long killStreakLength;
    private Long killerID;
    private Position position;
    private Long shutdownBounty;
    private List<VictimDamage> victimDamageDealt;
    private List<VictimDamage> victimDamageReceived;
    private Long victimID;
    private KillType killType;
    private List<Long> assistingParticipantIDS;
    private Long afterID;
    private Long beforeID;
    private Long goldGain;
    private LaneType laneType;
    private Long teamID;
    private Long killerTeamID;
    private String monsterType;
    private Long multiKillLength;
    private String monsterSubType;
    private BuildingType buildingType;
    private String towerType;
    private Long actualStartTime;
    private Long gameID;
    private Long winningTeam;

    @JsonProperty("realTimestamp")
    public Long getRealTimestamp() { return realTimestamp; }
    @JsonProperty("realTimestamp")
    public void setRealTimestamp(Long value) { this.realTimestamp = value; }

    @JsonProperty("timestamp")
    public long getTimestamp() { return timestamp; }
    @JsonProperty("timestamp")
    public void setTimestamp(long value) { this.timestamp = value; }

    @JsonProperty("type")
    public EventType getType() { return type; }
    @JsonProperty("type")
    public void setType(EventType value) { this.type = value; }

    @JsonProperty("itemId")
    public Long getItemID() { return itemID; }
    @JsonProperty("itemId")
    public void setItemID(Long value) { this.itemID = value; }

    @JsonProperty("participantId")
    public Long getParticipantID() { return participantID; }
    @JsonProperty("participantId")
    public void setParticipantID(Long value) { this.participantID = value; }

    @JsonProperty("levelUpType")
    public LevelUpType getLevelUpType() { return levelUpType; }
    @JsonProperty("levelUpType")
    public void setLevelUpType(LevelUpType value) { this.levelUpType = value; }

    @JsonProperty("skillSlot")
    public Long getSkillSlot() { return skillSlot; }
    @JsonProperty("skillSlot")
    public void setSkillSlot(Long value) { this.skillSlot = value; }

    @JsonProperty("creatorId")
    public Long getCreatorID() { return creatorID; }
    @JsonProperty("creatorId")
    public void setCreatorID(Long value) { this.creatorID = value; }

    @JsonProperty("wardType")
    public WardType getWardType() { return wardType; }
    @JsonProperty("wardType")
    public void setWardType(WardType value) { this.wardType = value; }

    @JsonProperty("level")
    public Long getLevel() { return level; }
    @JsonProperty("level")
    public void setLevel(Long value) { this.level = value; }

    @JsonProperty("bounty")
    public Long getBounty() { return bounty; }
    @JsonProperty("bounty")
    public void setBounty(Long value) { this.bounty = value; }

    @JsonProperty("killStreakLength")
    public Long getKillStreakLength() { return killStreakLength; }
    @JsonProperty("killStreakLength")
    public void setKillStreakLength(Long value) { this.killStreakLength = value; }

    @JsonProperty("killerId")
    public Long getKillerID() { return killerID; }
    @JsonProperty("killerId")
    public void setKillerID(Long value) { this.killerID = value; }

    @JsonProperty("position")
    public Position getPosition() { return position; }
    @JsonProperty("position")
    public void setPosition(Position value) { this.position = value; }

    @JsonProperty("shutdownBounty")
    public Long getShutdownBounty() { return shutdownBounty; }
    @JsonProperty("shutdownBounty")
    public void setShutdownBounty(Long value) { this.shutdownBounty = value; }

    @JsonProperty("victimDamageDealt")
    public List<VictimDamage> getVictimDamageDealt() { return victimDamageDealt; }
    @JsonProperty("victimDamageDealt")
    public void setVictimDamageDealt(List<VictimDamage> value) { this.victimDamageDealt = value; }

    @JsonProperty("victimDamageReceived")
    public List<VictimDamage> getVictimDamageReceived() { return victimDamageReceived; }
    @JsonProperty("victimDamageReceived")
    public void setVictimDamageReceived(List<VictimDamage> value) { this.victimDamageReceived = value; }

    @JsonProperty("victimId")
    public Long getVictimID() { return victimID; }
    @JsonProperty("victimId")
    public void setVictimID(Long value) { this.victimID = value; }

    @JsonProperty("killType")
    public KillType getKillType() { return killType; }
    @JsonProperty("killType")
    public void setKillType(KillType value) { this.killType = value; }

    @JsonProperty("assistingParticipantIds")
    public List<Long> getAssistingParticipantIDS() { return assistingParticipantIDS; }
    @JsonProperty("assistingParticipantIds")
    public void setAssistingParticipantIDS(List<Long> value) { this.assistingParticipantIDS = value; }

    @JsonProperty("afterId")
    public Long getAfterID() { return afterID; }
    @JsonProperty("afterId")
    public void setAfterID(Long value) { this.afterID = value; }

    @JsonProperty("beforeId")
    public Long getBeforeID() { return beforeID; }
    @JsonProperty("beforeId")
    public void setBeforeID(Long value) { this.beforeID = value; }

    @JsonProperty("goldGain")
    public Long getGoldGain() { return goldGain; }
    @JsonProperty("goldGain")
    public void setGoldGain(Long value) { this.goldGain = value; }

    @JsonProperty("laneType")
    public LaneType getLaneType() { return laneType; }
    @JsonProperty("laneType")
    public void setLaneType(LaneType value) { this.laneType = value; }

    @JsonProperty("teamId")
    public Long getTeamID() { return teamID; }
    @JsonProperty("teamId")
    public void setTeamID(Long value) { this.teamID = value; }

    @JsonProperty("killerTeamId")
    public Long getKillerTeamID() { return killerTeamID; }
    @JsonProperty("killerTeamId")
    public void setKillerTeamID(Long value) { this.killerTeamID = value; }

    @JsonProperty("monsterType")
    public String getMonsterType() { return monsterType; }
    @JsonProperty("monsterType")
    public void setMonsterType(String value) { this.monsterType = value; }

    @JsonProperty("multiKillLength")
    public Long getMultiKillLength() { return multiKillLength; }
    @JsonProperty("multiKillLength")
    public void setMultiKillLength(Long value) { this.multiKillLength = value; }

    @JsonProperty("monsterSubType")
    public String getMonsterSubType() { return monsterSubType; }
    @JsonProperty("monsterSubType")
    public void setMonsterSubType(String value) { this.monsterSubType = value; }

    @JsonProperty("buildingType")
    public BuildingType getBuildingType() { return buildingType; }
    @JsonProperty("buildingType")
    public void setBuildingType(BuildingType value) { this.buildingType = value; }

    @JsonProperty("towerType")
    public String getTowerType() { return towerType; }
    @JsonProperty("towerType")
    public void setTowerType(String value) { this.towerType = value; }

    @JsonProperty("actualStartTime")
    public Long getActualStartTime() { return actualStartTime; }
    @JsonProperty("actualStartTime")
    public void setActualStartTime(Long value) { this.actualStartTime = value; }

    @JsonProperty("gameId")
    public Long getGameID() { return gameID; }
    @JsonProperty("gameId")
    public void setGameID(Long value) { this.gameID = value; }

    @JsonProperty("winningTeam")
    public Long getWinningTeam() { return winningTeam; }
    @JsonProperty("winningTeam")
    public void setWinningTeam(Long value) { this.winningTeam = value; }
}
