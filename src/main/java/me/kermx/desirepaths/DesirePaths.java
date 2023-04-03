package me.kermx.desirepaths;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class DesirePaths extends JavaPlugin {

    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private enum modifierType{NO_BOOTS, LEATHER_BOOTS, HAS_BOOTS, FEATHER_FALLING, RIDING_HORSE, RIDING_BOAT, RIDING_PIG}


    @Override
    public void onEnable() {
        // Load config
        saveDefaultConfig();
        reloadConfig();
        //config attemptFrequency value
        int attemptFrequency = getConfig().getInt("attemptFrequency");
        //config chanceModifier values
        int noBootsChance = getConfig().getInt("chanceModifiers.NO_BOOTS");
        int leatherBootsChance = getConfig().getInt("chanceModifiers.LEATHER_BOOTS");
        int hasBootsChance = getConfig().getInt("chanceModifiers.HAS_BOOTS");
        int featherFallingChance = getConfig().getInt("chanceModifiers.FEATHER_FALLING");
        int ridingHorseChance = getConfig().getInt("chanceModifiers.RIDING_HORSE");
        int ridingBoatChance = getConfig().getInt("chanceModifiers.RIDING_BOAT");
        int ridingPigChance = getConfig().getInt("chanceModifiers.RIDING_PIG");
        int sprintingBlockBelowChance = getConfig().getInt("chanceModifiers.SPRINTING_BLOCK_BELOW");
        int sprintingBlockAtFeetChance = getConfig().getInt("chanceModifiers.SPRINTING_BLOCK_AT_FEET");

        //config blockModifications Lists
        List<String> blockBelowSwitcherConfig = getConfig().getStringList("blockModifications.blockBelowModifications");
        List<String> blockAtFeetSwitcherConfig = getConfig().getStringList("blockModifications.blockAtFeetModifications");

        // Plugin startup logic
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerHandler(player, noBootsChance, leatherBootsChance, hasBootsChance, featherFallingChance, ridingHorseChance, ridingBoatChance, ridingPigChance, sprintingBlockBelowChance, sprintingBlockAtFeetChance, blockAtFeetSwitcherConfig, blockBelowSwitcherConfig);
            }
        }, 0L, attemptFrequency);
        Bukkit.getLogger().info("DesirePaths enabled successfully!");
    }

    private void playerHandler(Player player, int noBootsChance, int leatherBootsChance, int hasBootsChance, int featherFallingChance, int ridingHorseChance, int ridingBoatChance, int ridingPigChance, int sprintingBlockBelowChance, int sprintingBlockAtFeetChance, List<String> blockAtFeetSwitcherConfig, List<String> blockBelowSwitcherConfig) {
        if (player.getGameMode() != GameMode.SURVIVAL || player.hasPermission("desirepaths.exempt"))
            return;
        int chance = getChance(player, noBootsChance, leatherBootsChance, hasBootsChance, featherFallingChance, ridingHorseChance, ridingBoatChance, ridingPigChance);
        int randomNum = random.nextInt(100);
        Bukkit.getScheduler().runTask(this,()-> blockBelowHandler(player.getLocation().getBlock().getRelative(BlockFace.DOWN), player, chance, randomNum, sprintingBlockBelowChance, blockBelowSwitcherConfig));
        Bukkit.getScheduler().runTask(this,()-> blockAtFeetHandler(player.getLocation().getBlock(),player, chance, randomNum, sprintingBlockAtFeetChance, blockAtFeetSwitcherConfig));
    }
    public static int getChance(Player player, int noBootsChance, int leatherBootsChance, int hasBootsChance, int featherFallingChance, int ridingHorseChance, int ridingBoatChance, int ridingPigChance) {
        return switch (getModifier(player)) {
            case RIDING_HORSE -> ridingHorseChance;
            case RIDING_BOAT -> ridingBoatChance;
            case RIDING_PIG -> ridingPigChance;
            case FEATHER_FALLING -> featherFallingChance;
            case HAS_BOOTS -> hasBootsChance;
            case LEATHER_BOOTS -> leatherBootsChance;
            case NO_BOOTS -> noBootsChance;
        };
    }
    //determine modifier to use for chance
    private static modifierType getModifier(Player player){
        if (player.getVehicle() instanceof Horse)
            return modifierType.RIDING_HORSE;
        if (player.getVehicle() instanceof Boat)
            return modifierType.RIDING_BOAT;
        if (player.getVehicle() instanceof Pig)
            return modifierType.RIDING_PIG;
        ItemStack boots = player.getInventory().getBoots();
        if (boots == null)
            return modifierType.NO_BOOTS;
        Material bootMaterial = boots.getType();
        Set<Material> bootMaterials = EnumSet.of(Material.IRON_BOOTS,Material.GOLDEN_BOOTS,Material.DIAMOND_BOOTS,Material.NETHERITE_BOOTS,Material.LEATHER_BOOTS);
        Optional<Enchantment> featherFallingEnchantment = boots.getEnchantments().keySet().stream().filter(Enchantment.PROTECTION_FALL::equals).findFirst();
        if (bootMaterials.contains(bootMaterial)){
            if (featherFallingEnchantment.isPresent()){
                return modifierType.FEATHER_FALLING;
            } else {
                return modifierType.HAS_BOOTS;
            }
        } else if (bootMaterial == Material.LEATHER_BOOTS){
            return modifierType.LEATHER_BOOTS;
        } else {
            return modifierType.NO_BOOTS;
        }
    }
    //Handle block at the players feet
    private void blockAtFeetHandler(Block block, Player player, int chance, int randomNum, int sprintingBlockAtFeetChance, List<String> blockAtFeetSwitcherConfig){
        if (!player.isSprinting() && randomNum < chance)
            blockAtFeetSwitcher(block, blockAtFeetSwitcherConfig);
        if (player.isSprinting() && randomNum < chance + sprintingBlockAtFeetChance)
            blockAtFeetSwitcher(block, blockAtFeetSwitcherConfig);
    }
    private void blockAtFeetSwitcher(Block block, List<String> blockAtFeetSwitcherConfig) {
        Material type = block.getType();
        Map<Material, Material> blockSwitcher = new HashMap<>();
        for (String switchCase : blockAtFeetSwitcherConfig) {
            String[] parts = switchCase.split(":");
            Material sourceMaterial = Material.matchMaterial(parts[0]);
            Material targetMaterial = Material.matchMaterial(parts[1]);
            if (sourceMaterial != null && targetMaterial != null) {
                blockSwitcher.put(sourceMaterial, targetMaterial);
            } else {
                getLogger().warning("Invalid block switch case in blockAtFeetModifications: " + switchCase);
            }
        }
        Material targetMaterial = blockSwitcher.get(type);
        if (targetMaterial != null) {
            block.setType(targetMaterial);
        }
    }

    //Handle block below the player
    private void blockBelowHandler(Block block, Player player, int chance, int randomNum, int sprintingBlockBelowChance, List<String> blockBelowSwitcherConfig){
        if (player.isSneaking() || player.isSwimming())
            return;
        if (!player.isSprinting() && randomNum < chance)
            blockBelowSwitcher(block, blockBelowSwitcherConfig);
        if (player.isSprinting() && randomNum < chance + sprintingBlockBelowChance)
            blockBelowSwitcher(block, blockBelowSwitcherConfig);
    }
    private void blockBelowSwitcher(Block block, List<String> blockBelowSwitcherConfig) {
        Material type = block.getType();
        Map<Material, Material> blockSwitcher = new HashMap<>();
        for (String switchCase : blockBelowSwitcherConfig) {
            String[] parts = switchCase.split(":");
            Material sourceMaterial = Material.matchMaterial(parts[0]);
            Material targetMaterial = Material.matchMaterial(parts[1]);
            if (sourceMaterial != null && targetMaterial != null) {
                blockSwitcher.put(sourceMaterial, targetMaterial);
            } else {
                getLogger().warning("Invalid block switch case in blockBelowModifications: " + switchCase);
            }
        }
        Material targetMaterial = blockSwitcher.get(type);
        if (targetMaterial != null) {
            block.setType(targetMaterial);
        }
    }


    @Override
    public void onDisable() {
        Bukkit.getLogger().info("DesirePaths disabled!");
        // Plugin shutdown logic
    }
}
