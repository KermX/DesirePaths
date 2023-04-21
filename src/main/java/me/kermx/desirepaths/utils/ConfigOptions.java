package me.kermx.desirepaths.utils;

import me.kermx.desirepaths.DesirePaths;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public enum ConfigOptions {

    ATTEMPT_FREQUENCY("attemptFrequency"),
    DISABLED_WORLDS("disabledWorlds"),
    NO_BOOTS_CHANCE("chanceModifiers.NO_BOOTS"),
    LEATHER_BOOTS_CHANCE("chanceModifiers.LEATHER_BOOTS"),
    HAS_BOOTS_CHANCE("chanceModifiers.HAS_BOOTS"),
    FEATHER_FALLING_CHANCE("chanceModifiers.FEATHER_FALLING"),
    RIDING_HORSE_CHANCE("chanceModifiers.RIDING_HORSE"),
    RIDING_BOAT_CHANCE("chanceModifiers.RIDING_BOAT"),
    RIDING_PIG_CHANCE("chanceModifiers.RIDING_PIG"),
    SPRINTING_BLOCK_BELOW_CHANCE("chanceModifiers.SPRINTING_BLOCK_BELOW"),
    SPRINTING_BLOCK_AT_FEET_CHANCE("chanceModifiers.SPRINTING_BLOCK_AT_FEET"),
    BLOCK_BELOW_SWITCHER_LIST("blockModifications.blockBelowModifications"),
    BLOCK_AT_FEET_SWITCHER_LIST("blockModifications.blockAtFeetModifications"),
    PATHS_WHERE_ONLY_PLAYER_CAN_BREAK("townyModifiers.pathsOnlyWherePlayerCanBreak");

    public static final Map<ConfigOptions, Object> cachedValues = new HashMap<>();
    private final String configOption;

    ConfigOptions(String configOption) {
        this.configOption = configOption;
    }

    public <T> T getValue(Class<T> castType) {
        try {
            Object object = cachedValues.containsKey(this) ? cachedValues.get(this) : DesirePaths.getInstance().getConfig().get(configOption);
            T t = castType.cast(object);
            cachedValues.put(this, t);
            return t;
        } catch (ClassCastException e) {
            DesirePaths.getInstance().getLogger().severe("Invalid config option type! " + configOption + " is not a " + castType.getSimpleName() + "!");
            return null;
        }
    }
}
