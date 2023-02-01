package apiModel.matchTimeline;

import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum EventType {
    BUILDING_KILL, CHAMPION_KILL, CHAMPION_SPECIAL_KILL, ELITE_MONSTER_KILL, GAME_END, ITEM_DESTROYED, ITEM_PURCHASED, ITEM_SOLD, ITEM_UNDO, LEVEL_UP, PAUSE_END, SKILL_LEVEL_UP, TURRET_PLATE_DESTROYED, WARD_KILL, WARD_PLACED;

    @JsonValue
    public String toValue() {
        switch (this) {
            case BUILDING_KILL: return "BUILDING_KILL";
            case CHAMPION_KILL: return "CHAMPION_KILL";
            case CHAMPION_SPECIAL_KILL: return "CHAMPION_SPECIAL_KILL";
            case ELITE_MONSTER_KILL: return "ELITE_MONSTER_KILL";
            case GAME_END: return "GAME_END";
            case ITEM_DESTROYED: return "ITEM_DESTROYED";
            case ITEM_PURCHASED: return "ITEM_PURCHASED";
            case ITEM_SOLD: return "ITEM_SOLD";
            case ITEM_UNDO: return "ITEM_UNDO";
            case LEVEL_UP: return "LEVEL_UP";
            case PAUSE_END: return "PAUSE_END";
            case SKILL_LEVEL_UP: return "SKILL_LEVEL_UP";
            case TURRET_PLATE_DESTROYED: return "TURRET_PLATE_DESTROYED";
            case WARD_KILL: return "WARD_KILL";
            case WARD_PLACED: return "WARD_PLACED";
        }
        return null;
    }

    @JsonCreator
    public static EventType forValue(String value) throws IOException {
        if (value.equals("BUILDING_KILL")) return BUILDING_KILL;
        if (value.equals("CHAMPION_KILL")) return CHAMPION_KILL;
        if (value.equals("CHAMPION_SPECIAL_KILL")) return CHAMPION_SPECIAL_KILL;
        if (value.equals("ELITE_MONSTER_KILL")) return ELITE_MONSTER_KILL;
        if (value.equals("GAME_END")) return GAME_END;
        if (value.equals("ITEM_DESTROYED")) return ITEM_DESTROYED;
        if (value.equals("ITEM_PURCHASED")) return ITEM_PURCHASED;
        if (value.equals("ITEM_SOLD")) return ITEM_SOLD;
        if (value.equals("ITEM_UNDO")) return ITEM_UNDO;
        if (value.equals("LEVEL_UP")) return LEVEL_UP;
        if (value.equals("PAUSE_END")) return PAUSE_END;
        if (value.equals("SKILL_LEVEL_UP")) return SKILL_LEVEL_UP;
        if (value.equals("TURRET_PLATE_DESTROYED")) return TURRET_PLATE_DESTROYED;
        if (value.equals("WARD_KILL")) return WARD_KILL;
        if (value.equals("WARD_PLACED")) return WARD_PLACED;
        throw new IOException("Cannot deserialize EventType");
    }
}
