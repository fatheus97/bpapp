package apiModel.riot.matchV5.timeline;

import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum BuildingType {
    INHIBITOR_BUILDING, TOWER_BUILDING;

    @JsonValue
    public String toValue() {
        switch (this) {
            case INHIBITOR_BUILDING: return "INHIBITOR_BUILDING";
            case TOWER_BUILDING: return "TOWER_BUILDING";
        }
        return null;
    }

    @JsonCreator
    public static BuildingType forValue(String value) throws IOException {
        if (value.equals("INHIBITOR_BUILDING")) return INHIBITOR_BUILDING;
        if (value.equals("TOWER_BUILDING")) return TOWER_BUILDING;
        throw new IOException("Cannot deserialize BuildingType");
    }
}
