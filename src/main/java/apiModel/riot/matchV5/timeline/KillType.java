package apiModel.riot.matchV5.timeline;

import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum KillType {
    KILL_ACE, KILL_FIRST_BLOOD, KILL_MULTI;

    @JsonValue
    public String toValue() {
        switch (this) {
            case KILL_ACE: return "KILL_ACE";
            case KILL_FIRST_BLOOD: return "KILL_FIRST_BLOOD";
            case KILL_MULTI: return "KILL_MULTI";
        }
        return null;
    }

    @JsonCreator
    public static KillType forValue(String value) throws IOException {
        if (value.equals("KILL_ACE")) return KILL_ACE;
        if (value.equals("KILL_FIRST_BLOOD")) return KILL_FIRST_BLOOD;
        if (value.equals("KILL_MULTI")) return KILL_MULTI;
        throw new IOException("Cannot deserialize KillType");
    }
}
