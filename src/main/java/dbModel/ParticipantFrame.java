package dbModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.Map;
@Entity
@Table(name = "participantFrames")
public class ParticipantFrame {
    @ElementCollection
    private Map<String, Long> championStats;
    private long currentGold;
    @ElementCollection
    private Map<String, Long> damageStats;
    private long goldPerSecond;
    private long jungleMinionsKilled;
    private long level;
    private long minionsKilled;
    private long participantId;
    @Embedded
    private Position position;
    private long timeEnemySpentControlled;
    private long totalGold;
    private long xp;
    @Id
    @GeneratedValue
    private Long id;

    @JsonProperty("championStats")
    public Map<String, Long> getChampionStats() { return championStats; }
    @JsonProperty("championStats")
    public void setChampionStats(Map<String, Long> value) { this.championStats = value; }

    @JsonProperty("currentGold")
    public long getCurrentGold() { return currentGold; }
    @JsonProperty("currentGold")
    public void setCurrentGold(long value) { this.currentGold = value; }

    @JsonProperty("damageStats")
    public Map<String, Long> getDamageStats() { return damageStats; }
    @JsonProperty("damageStats")
    public void setDamageStats(Map<String, Long> value) { this.damageStats = value; }

    @JsonProperty("goldPerSecond")
    public long getGoldPerSecond() { return goldPerSecond; }
    @JsonProperty("goldPerSecond")
    public void setGoldPerSecond(long value) { this.goldPerSecond = value; }

    @JsonProperty("jungleMinionsKilled")
    public long getJungleMinionsKilled() { return jungleMinionsKilled; }
    @JsonProperty("jungleMinionsKilled")
    public void setJungleMinionsKilled(long value) { this.jungleMinionsKilled = value; }

    @JsonProperty("level")
    public long getLevel() { return level; }
    @JsonProperty("level")
    public void setLevel(long value) { this.level = value; }

    @JsonProperty("minionsKilled")
    public long getMinionsKilled() { return minionsKilled; }
    @JsonProperty("minionsKilled")
    public void setMinionsKilled(long value) { this.minionsKilled = value; }

    @JsonProperty("participantId")
    public long getParticipantId() { return participantId; }
    @JsonProperty("participantId")
    public void setParticipantId(long value) { this.participantId = value; }

    @JsonProperty("position")
    public Position getPosition() { return position; }
    @JsonProperty("position")
    public void setPosition(Position value) { this.position = value; }

    @JsonProperty("timeEnemySpentControlled")
    public long getTimeEnemySpentControlled() { return timeEnemySpentControlled; }
    @JsonProperty("timeEnemySpentControlled")
    public void setTimeEnemySpentControlled(long value) { this.timeEnemySpentControlled = value; }

    @JsonProperty("totalGold")
    public long getTotalGold() { return totalGold; }
    @JsonProperty("totalGold")
    public void setTotalGold(long value) { this.totalGold = value; }

    @JsonProperty("xp")
    public long getXP() { return xp; }
    @JsonProperty("xp")
    public void setXP(long value) { this.xp = value; }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ParticipantFrame{" +
                "championStats=" + championStats +
                ", currentGold=" + currentGold +
                ", damageStats=" + damageStats +
                ", goldPerSecond=" + goldPerSecond +
                ", jungleMinionsKilled=" + jungleMinionsKilled +
                ", level=" + level +
                ", minionsKilled=" + minionsKilled +
                ", participantID=" + participantId +
                ", position=" + position +
                ", timeEnemySpentControlled=" + timeEnemySpentControlled +
                ", totalGold=" + totalGold +
                ", xp=" + xp +
                ", id=" + id +
                '}' + "\n";
    }
}
