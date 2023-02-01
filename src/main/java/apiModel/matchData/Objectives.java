package apiModel.matchData;

import com.fasterxml.jackson.annotation.*;

public class Objectives {
    private Baron baron;
    private Baron champion;
    private Baron dragon;
    private Baron inhibitor;
    private Baron riftHerald;
    private Baron tower;

    @JsonProperty("baron")
    public Baron getBaron() { return baron; }
    @JsonProperty("baron")
    public void setBaron(Baron value) { this.baron = value; }

    @JsonProperty("champion")
    public Baron getChampion() { return champion; }
    @JsonProperty("champion")
    public void setChampion(Baron value) { this.champion = value; }

    @JsonProperty("dragon")
    public Baron getDragon() { return dragon; }
    @JsonProperty("dragon")
    public void setDragon(Baron value) { this.dragon = value; }

    @JsonProperty("inhibitor")
    public Baron getInhibitor() { return inhibitor; }
    @JsonProperty("inhibitor")
    public void setInhibitor(Baron value) { this.inhibitor = value; }

    @JsonProperty("riftHerald")
    public Baron getRiftHerald() { return riftHerald; }
    @JsonProperty("riftHerald")
    public void setRiftHerald(Baron value) { this.riftHerald = value; }

    @JsonProperty("tower")
    public Baron getTower() { return tower; }
    @JsonProperty("tower")
    public void setTower(Baron value) { this.tower = value; }
}
