package dbModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "participants")
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String summonerName;
    private String championName;
    private long participantId;
    private String puuid;
    @ManyToOne
    private Info info;

    public Participant() {
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public String getChampionName() {
        return championName;
    }
    @JsonProperty("participantId")
    public long getParticipantId() { return participantId; }
    @JsonProperty("participantId")
    public void setParticipantId(long value) { this.participantId = value; }

    @JsonProperty("puuid")
    public String getPuuid() { return puuid; }
    @JsonProperty("puuid")
    public void setPuuid(String value) { this.puuid = value; }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", summonerName='" + summonerName + '\'' +
                ", championName='" + championName + '\'' +
                '}';
    }
}
