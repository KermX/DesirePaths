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
        Bukkit.getLogger().info("DesirePaths enabled successfully!");
        // Plugin startup logic
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerHandler(player);
            }
        }, 0L, 10);

    }

    private void playerHandler(Player player){
        if (player.getGameMode() != GameMode.SURVIVAL)
            return;
        int chance = getChance(player);
        int randomNum = random.nextInt(100);
        Bukkit.getScheduler().runTask(this,()-> blockBelowHandler(player.getLocation().getBlock().getRelative(BlockFace.DOWN), player, chance, randomNum));
        Bukkit.getScheduler().runTask(this,()-> blockAtFeetHandler(player.getLocation().getBlock(),player, chance, randomNum));
    }
    public static int getChance(Player player){
        return switch (getModifier(player)) {
            case RIDING_HORSE -> 37;
            case RIDING_BOAT -> 99;
            case RIDING_PIG -> 30;
            case FEATHER_FALLING -> 10;
            case HAS_BOOTS -> 20;
            case LEATHER_BOOTS -> 11;
            case NO_BOOTS -> 3;
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
    private void blockAtFeetHandler(Block block, Player player, int chance, int randomNum){
        if (!player.isSprinting() && randomNum < chance)
            blockAtFeetSwitcher(block);
        if (player.isSprinting())
            blockAtFeetSwitcher(block);
    }
    private void blockAtFeetSwitcher(Block block){
        Material type = block.getType();
        block.setType(switch(type){
            case SNOW,GRASS,FERN -> Material.AIR;
            case TALL_GRASS -> Material.GRASS;
            default -> type;
        });
    }

    //Handle block below the player
    private void blockBelowHandler(Block block, Player player, int chance, int randomNum){
        if (player.isSneaking() || player.isSwimming())
            return;
        if (!player.isSprinting() && randomNum < chance)
            blockBelowSwitcher(block);
        if (player.isSprinting() && randomNum < 15 + chance)
            blockBelowSwitcher(block);
    }
    private void blockBelowSwitcher(Block block){
        Material type = block.getType();
        block.setType(switch(type){
            case GRASS_BLOCK -> Material.DIRT;
            case DIRT, PODZOL -> Material.COARSE_DIRT;
            case COARSE_DIRT -> Material.DIRT_PATH;
            case SANDSTONE -> Material.SAND;
            case CRIMSON_NYLIUM, WARPED_NYLIUM -> Material.NETHERRACK;
            case MYCELIUM -> Material.ROOTED_DIRT;
            case MAGMA_BLOCK -> Material.LAVA;
            default -> type;
        });
    }


    @Override
    public void onDisable() {
        Bukkit.getLogger().info("DesirePaths disabled!");
        // Plugin shutdown logic
    }
}
