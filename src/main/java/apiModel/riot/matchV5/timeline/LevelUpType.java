package apiModel.riot.matchV5.timeline;

import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum LevelUpType {
    EVOLVE, NORMAL;

    @JsonValue
    public String toValue() {
        switch (this) {
            case EVOLVE: return "EVOLVE";
            case NORMAL: return "NORMAL";
        }
        return null;
    }

    @JsonCreator
    public static LevelUpType forValue(String value) throws IOException {
        if (value.equals("EVOLVE")) return EVOLVE;
        if (value.equals("NORMAL")) return NORMAL;
        throw new IOException("Cannot deserialize LevelUpType");
    }
}
