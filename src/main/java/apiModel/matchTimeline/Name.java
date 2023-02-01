package apiModel.matchTimeline;

import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum Name {
    APHELIOS, ASHE, CAITLYN, FIORA, JAX, KASSADIN, LUX, MONKEY_KING, SRU_BARON, SRU_CHAOS_MINION_MELEE, SRU_CHAOS_MINION_RANGED, SRU_ORDER_MINION_MELEE, SRU_ORDER_MINION_RANGED, SRU_RAZORBEAK, SRU_RAZORBEAK_MINI, SYNDRA, TURRET, VI;

    @JsonValue
    public String toValue() {
        switch (this) {
            case APHELIOS: return "Aphelios";
            case ASHE: return "Ashe";
            case CAITLYN: return "Caitlyn";
            case FIORA: return "Fiora";
            case JAX: return "Jax";
            case KASSADIN: return "Kassadin";
            case LUX: return "Lux";
            case MONKEY_KING: return "MonkeyKing";
            case SRU_BARON: return "SRU_Baron";
            case SRU_CHAOS_MINION_MELEE: return "SRU_ChaosMinionMelee";
            case SRU_CHAOS_MINION_RANGED: return "SRU_ChaosMinionRanged";
            case SRU_ORDER_MINION_MELEE: return "SRU_OrderMinionMelee";
            case SRU_ORDER_MINION_RANGED: return "SRU_OrderMinionRanged";
            case SRU_RAZORBEAK: return "SRU_Razorbeak";
            case SRU_RAZORBEAK_MINI: return "SRU_RazorbeakMini";
            case SYNDRA: return "Syndra";
            case TURRET: return "Turret";
            case VI: return "Vi";
        }
        return null;
    }

    @JsonCreator
    public static Name forValue(String value) throws IOException {
        if (value.equals("Aphelios")) return APHELIOS;
        if (value.equals("Ashe")) return ASHE;
        if (value.equals("Caitlyn")) return CAITLYN;
        if (value.equals("Fiora")) return FIORA;
        if (value.equals("Jax")) return JAX;
        if (value.equals("Kassadin")) return KASSADIN;
        if (value.equals("Lux")) return LUX;
        if (value.equals("MonkeyKing")) return MONKEY_KING;
        if (value.equals("SRU_Baron")) return SRU_BARON;
        if (value.equals("SRU_ChaosMinionMelee")) return SRU_CHAOS_MINION_MELEE;
        if (value.equals("SRU_ChaosMinionRanged")) return SRU_CHAOS_MINION_RANGED;
        if (value.equals("SRU_OrderMinionMelee")) return SRU_ORDER_MINION_MELEE;
        if (value.equals("SRU_OrderMinionRanged")) return SRU_ORDER_MINION_RANGED;
        if (value.equals("SRU_Razorbeak")) return SRU_RAZORBEAK;
        if (value.equals("SRU_RazorbeakMini")) return SRU_RAZORBEAK_MINI;
        if (value.equals("Syndra")) return SYNDRA;
        if (value.equals("Turret")) return TURRET;
        if (value.equals("Vi")) return VI;
        throw new IOException("Cannot deserialize Name");
    }
}
