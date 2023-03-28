package dbModel;

import jakarta.persistence.*;

@Entity
@Table(name = "participants")
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String summonerName;
    private String championName;
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

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", summonerName='" + summonerName + '\'' +
                ", championName='" + championName + '\'' +
                '}';
    }
}
