package me.kermx.desirepaths.path;

import me.kermx.desirepaths.utils.ConfigOptions;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum PathModifierType {
    NO_BOOTS(null, () -> ConfigOptions.NO_BOOTS_CHANCE.getValue(int.class)),
    LEATHER_BOOTS(null, () -> ConfigOptions.LEATHER_BOOTS_CHANCE.getValue(int.class)),
    HAS_BOOTS(null, () -> ConfigOptions.HAS_BOOTS_CHANCE.getValue(int.class)),
    FEATHER_FALLING(null, () -> ConfigOptions.FEATHER_FALLING_CHANCE.getValue(int.class)),
    RIDING_HORSE(Horse.class, () -> ConfigOptions.RIDING_HORSE_CHANCE.getValue(int.class)),
    RIDING_BOAT(Boat.class, () -> ConfigOptions.RIDING_BOAT_CHANCE.getValue(int.class)),
    RIDING_PIG(Pig.class, () -> ConfigOptions.RIDING_PIG_CHANCE.getValue(int.class));

    private final Class<? extends Entity> entityType;
    private final Supplier<Integer> chance;
    private static final List<Material> bootMaterials = Arrays.stream(Material.values()).filter(x -> x.name().endsWith("_BOOTS") && !x.equals(Material.LEATHER_BOOTS)).collect(Collectors.toList());
    ;

    PathModifierType(Class<? extends Entity> entityType, Supplier<Integer> chance) {
        this.entityType = entityType;
        this.chance = chance;
    }

    public static PathModifierType getModifier(Player player) {
        Entity vehicle = player.getVehicle();
        for (PathModifierType type : (vehicle == null ? new PathModifierType[0] : values())) {
            if (type.entityType != null && type.entityType.isAssignableFrom(vehicle.getClass())) {
                return type;
            }
        }
        ItemStack boots = player.getInventory().getBoots();
        if (boots == null) {
            return NO_BOOTS;
        }
        Material bootMaterial = boots.getType();
        Optional<Enchantment> featherFallingEnchantment = boots.getEnchantments().keySet().stream().filter(Enchantment.PROTECTION_FALL::equals).findFirst();
        if (!bootMaterials.contains(bootMaterial)) {
            return bootMaterial == Material.LEATHER_BOOTS ? PathModifierType.LEATHER_BOOTS : PathModifierType.NO_BOOTS;
        }
        if (featherFallingEnchantment.isPresent()) {
            return PathModifierType.FEATHER_FALLING;
        }
        return PathModifierType.HAS_BOOTS;
    }

    public int getChance() {
        return chance.get();
    }
}
