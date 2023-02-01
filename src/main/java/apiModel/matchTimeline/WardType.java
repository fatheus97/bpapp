package apiModel.matchTimeline;

import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum WardType {
    BLUE_TRINKET, CONTROL_WARD, SIGHT_WARD, UNDEFINED, YELLOW_TRINKET;

    @JsonValue
    public String toValue() {
        switch (this) {
            case BLUE_TRINKET: return "BLUE_TRINKET";
            case CONTROL_WARD: return "CONTROL_WARD";
            case SIGHT_WARD: return "SIGHT_WARD";
            case UNDEFINED: return "UNDEFINED";
            case YELLOW_TRINKET: return "YELLOW_TRINKET";
        }
        return null;
    }

    @JsonCreator
    public static WardType forValue(String value) throws IOException {
        if (value.equals("BLUE_TRINKET")) return BLUE_TRINKET;
        if (value.equals("CONTROL_WARD")) return CONTROL_WARD;
        if (value.equals("SIGHT_WARD")) return SIGHT_WARD;
        if (value.equals("UNDEFINED")) return UNDEFINED;
        if (value.equals("YELLOW_TRINKET")) return YELLOW_TRINKET;
        throw new IOException("Cannot deserialize WardType");
    }
}
