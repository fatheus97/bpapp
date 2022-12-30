package model;

import com.fasterxml.jackson.annotation.*;

public class Selection {
    private long perk;
    private long var1;
    private long var2;
    private long var3;

    @JsonProperty("perk")
    public long getPerk() { return perk; }
    @JsonProperty("perk")
    public void setPerk(long value) { this.perk = value; }

    @JsonProperty("var1")
    public long getVar1() { return var1; }
    @JsonProperty("var1")
    public void setVar1(long value) { this.var1 = value; }

    @JsonProperty("var2")
    public long getVar2() { return var2; }
    @JsonProperty("var2")
    public void setVar2(long value) { this.var2 = value; }

    @JsonProperty("var3")
    public long getVar3() { return var3; }
    @JsonProperty("var3")
    public void setVar3(long value) { this.var3 = value; }
}
