package apiModel.matchTimeline;

import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum LevelUpType {
    NORMAL;

    @JsonValue
    public String toValue() {
        switch (this) {
            case NORMAL: return "NORMAL";
        }
        return null;
    }

    @JsonCreator
    public static LevelUpType forValue(String value) throws IOException {
        if (value.equals("NORMAL")) return NORMAL;
        throw new IOException("Cannot deserialize LevelUpType");
    }
}
