package apiModel.riot.matchV5.timeline;

import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum LaneType {
    BOT_LANE, MID_LANE, TOP_LANE;

    @JsonValue
    public String toValue() {
        switch (this) {
            case BOT_LANE: return "BOT_LANE";
            case MID_LANE: return "MID_LANE";
            case TOP_LANE: return "TOP_LANE";
        }
        return null;
    }

    @JsonCreator
    public static LaneType forValue(String value) throws IOException {
        if (value.equals("BOT_LANE")) return BOT_LANE;
        if (value.equals("MID_LANE")) return MID_LANE;
        if (value.equals("TOP_LANE")) return TOP_LANE;
        throw new IOException("Cannot deserialize LaneType");
    }
}
