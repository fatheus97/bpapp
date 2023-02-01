package apiModel.matchTimeline;

import com.fasterxml.jackson.annotation.*;

public class VictimDamage {
    private boolean basic;
    private long magicDamage;
    private Name name;
    private long participantID;
    private long physicalDamage;
    private String spellName;
    private long spellSlot;
    private long trueDamage;
    private VictimDamageDealtType type;

    @JsonProperty("basic")
    public boolean getBasic() { return basic; }
    @JsonProperty("basic")
    public void setBasic(boolean value) { this.basic = value; }

    @JsonProperty("magicDamage")
    public long getMagicDamage() { return magicDamage; }
    @JsonProperty("magicDamage")
    public void setMagicDamage(long value) { this.magicDamage = value; }

    @JsonProperty("name")
    public Name getName() { return name; }
    @JsonProperty("name")
    public void setName(Name value) { this.name = value; }

    @JsonProperty("participantId")
    public long getParticipantID() { return participantID; }
    @JsonProperty("participantId")
    public void setParticipantID(long value) { this.participantID = value; }

    @JsonProperty("physicalDamage")
    public long getPhysicalDamage() { return physicalDamage; }
    @JsonProperty("physicalDamage")
    public void setPhysicalDamage(long value) { this.physicalDamage = value; }

    @JsonProperty("spellName")
    public String getSpellName() { return spellName; }
    @JsonProperty("spellName")
    public void setSpellName(String value) { this.spellName = value; }

    @JsonProperty("spellSlot")
    public long getSpellSlot() { return spellSlot; }
    @JsonProperty("spellSlot")
    public void setSpellSlot(long value) { this.spellSlot = value; }

    @JsonProperty("trueDamage")
    public long getTrueDamage() { return trueDamage; }
    @JsonProperty("trueDamage")
    public void setTrueDamage(long value) { this.trueDamage = value; }

    @JsonProperty("type")
    public VictimDamageDealtType getType() { return type; }
    @JsonProperty("type")
    public void setType(VictimDamageDealtType value) { this.type = value; }
}
