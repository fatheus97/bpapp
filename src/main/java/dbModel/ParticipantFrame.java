package dbModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "participantFrames")
public class ParticipantFrame {
    private long participantId;
    @Embedded
    private Position position;
    private long totalGold;
    @Id
    @GeneratedValue
    private Long id;

    @JsonProperty("participantId")
    public long getParticipantId() {
        return participantId;
    }

    @JsonProperty("participantId")
    public void setParticipantId(long value) {
        this.participantId = value;
    }

    @JsonProperty("position")
    public Position getPosition() {
        return position;
    }

    @JsonProperty("position")
    public void setPosition(Position value) {
        this.position = value;
    }

    @JsonProperty("totalGold")
    public long getTotalGold() {
        return totalGold;
    }

    @JsonProperty("totalGold")
    public void setTotalGold(long value) {
        this.totalGold = value;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
