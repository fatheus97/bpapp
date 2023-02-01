package apiModel.matchTimeline;

import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum VictimDamageDealtType {
    MINION, MONSTER, OTHER, TOWER;

    @JsonValue
    public String toValue() {
        switch (this) {
            case MINION: return "MINION";
            case MONSTER: return "MONSTER";
            case OTHER: return "OTHER";
            case TOWER: return "TOWER";
        }
        return null;
    }

    @JsonCreator
    public static VictimDamageDealtType forValue(String value) throws IOException {
        if (value.equals("MINION")) return MINION;
        if (value.equals("MONSTER")) return MONSTER;
        if (value.equals("OTHER")) return OTHER;
        if (value.equals("TOWER")) return TOWER;
        throw new IOException("Cannot deserialize VictimDamageDealtType");
    }
}
