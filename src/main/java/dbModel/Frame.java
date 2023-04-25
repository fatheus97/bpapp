package dbModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Map;

@Entity
@Table(name = "frames")
public class Frame {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private Map<String, ParticipantFrame> participantFrames;
    private long timestamp;

    @JsonProperty("participantFrames")
    public Map<String, ParticipantFrame> getParticipantFrames() {
        return participantFrames;
    }

    @JsonProperty("participantFrames")
    public void setParticipantFrames(Map<String, ParticipantFrame> value) {
        this.participantFrames = value;
    }

    @JsonProperty("timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(long value) {
        this.timestamp = value;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
