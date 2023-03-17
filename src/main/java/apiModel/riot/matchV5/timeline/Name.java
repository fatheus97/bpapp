package apiModel.riot.matchV5.timeline;

import java.io.IOException;
import com.fasterxml.jackson.annotation.*;

public enum Name {
    BELVETH, BELVETH_VOIDLING, GRAGAS, HECARIM, JAYCE, KAISA, NAUTILUS, SEJUANI, SRU_CHAOS_MINION_MELEE, SRU_CHAOS_MINION_RANGED, SRU_DRAGON_EARTH, SRU_GROMP, SRU_KRUG, SRU_KRUG_MINI, SRU_KRUG_MINI_MINI, SRU_MURKWOLF, SRU_MURKWOLF_MINI, SRU_ORDER_MINION_MELEE, SRU_ORDER_MINION_RANGED, SRU_RAZORBEAK, SRU_RAZORBEAK_MINI, SRU_RED, SRU_RIFT_HERALD, TRYNDAMERE, TURRET, VEIGAR, ZED;

    @JsonValue
    public String toValue() {
        switch (this) {
            case BELVETH: return "Belveth";
            case BELVETH_VOIDLING: return "BelvethVoidling";
            case GRAGAS: return "Gragas";
            case HECARIM: return "Hecarim";
            case JAYCE: return "Jayce";
            case KAISA: return "Kaisa";
            case NAUTILUS: return "Nautilus";
            case SEJUANI: return "Sejuani";
            case SRU_CHAOS_MINION_MELEE: return "SRU_ChaosMinionMelee";
            case SRU_CHAOS_MINION_RANGED: return "SRU_ChaosMinionRanged";
            case SRU_DRAGON_EARTH: return "SRU_Dragon_Earth";
            case SRU_GROMP: return "SRU_Gromp";
            case SRU_KRUG: return "SRU_Krug";
            case SRU_KRUG_MINI: return "SRU_KrugMini";
            case SRU_KRUG_MINI_MINI: return "SRU_KrugMiniMini";
            case SRU_MURKWOLF: return "SRU_Murkwolf";
            case SRU_MURKWOLF_MINI: return "SRU_MurkwolfMini";
            case SRU_ORDER_MINION_MELEE: return "SRU_OrderMinionMelee";
            case SRU_ORDER_MINION_RANGED: return "SRU_OrderMinionRanged";
            case SRU_RAZORBEAK: return "SRU_Razorbeak";
            case SRU_RAZORBEAK_MINI: return "SRU_RazorbeakMini";
            case SRU_RED: return "SRU_Red";
            case SRU_RIFT_HERALD: return "SRU_RiftHerald";
            case TRYNDAMERE: return "Tryndamere";
            case TURRET: return "Turret";
            case VEIGAR: return "Veigar";
            case ZED: return "Zed";
        }
        return null;
    }

    @JsonCreator
    public static Name forValue(String value) throws IOException {
        if (value.equals("Belveth")) return BELVETH;
        if (value.equals("BelvethVoidling")) return BELVETH_VOIDLING;
        if (value.equals("Gragas")) return GRAGAS;
        if (value.equals("Hecarim")) return HECARIM;
        if (value.equals("Jayce")) return JAYCE;
        if (value.equals("Kaisa")) return KAISA;
        if (value.equals("Nautilus")) return NAUTILUS;
        if (value.equals("Sejuani")) return SEJUANI;
        if (value.equals("SRU_ChaosMinionMelee")) return SRU_CHAOS_MINION_MELEE;
        if (value.equals("SRU_ChaosMinionRanged")) return SRU_CHAOS_MINION_RANGED;
        if (value.equals("SRU_Dragon_Earth")) return SRU_DRAGON_EARTH;
        if (value.equals("SRU_Gromp")) return SRU_GROMP;
        if (value.equals("SRU_Krug")) return SRU_KRUG;
        if (value.equals("SRU_KrugMini")) return SRU_KRUG_MINI;
        if (value.equals("SRU_KrugMiniMini")) return SRU_KRUG_MINI_MINI;
        if (value.equals("SRU_Murkwolf")) return SRU_MURKWOLF;
        if (value.equals("SRU_MurkwolfMini")) return SRU_MURKWOLF_MINI;
        if (value.equals("SRU_OrderMinionMelee")) return SRU_ORDER_MINION_MELEE;
        if (value.equals("SRU_OrderMinionRanged")) return SRU_ORDER_MINION_RANGED;
        if (value.equals("SRU_Razorbeak")) return SRU_RAZORBEAK;
        if (value.equals("SRU_RazorbeakMini")) return SRU_RAZORBEAK_MINI;
        if (value.equals("SRU_Red")) return SRU_RED;
        if (value.equals("SRU_RiftHerald")) return SRU_RIFT_HERALD;
        if (value.equals("Tryndamere")) return TRYNDAMERE;
        if (value.equals("Turret")) return TURRET;
        if (value.equals("Veigar")) return VEIGAR;
        if (value.equals("Zed")) return ZED;
        throw new IOException("Cannot deserialize Name");
    }
}
