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

import java.util.Random;

public final class DesirePaths extends JavaPlugin {
    private static final Random random = new Random();

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("DesirePaths enabled successfully!");
        // Plugin startup logic
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers())
                    DesirePaths.this.playerHandler(player);
            }
        }, 10L, 5L);

    }

    private void playerHandler(Player player){
        if (player.getGameMode() != GameMode.SURVIVAL)
            return;
        blockBelowHandler(player.getLocation().getBlock().getRelative(BlockFace.DOWN), player);
        blockAtFeetHandler(player.getLocation().getBlock(),player);
    }
    private void blockAtFeetHandler(Block block, Player player){
        if (player.isSneaking() || player.isSwimming())
            return;
        if (random.nextInt(100) < getChance(player) - 3 && block.getType() == Material.FARMLAND)
            block.setType(Material.MUD);
        if (random.nextInt(100) < getChance(player) + 30 || player.isSprinting())
            switch (block.getType()){
                case SNOW:
                case GRASS:
                case FERN:
                    block.setType(Material.AIR);
                    break;
                case TALL_GRASS:
                    block.setType(Material.GRASS);
                    break;
                default:
            }
    }
    private void globalSwitcher(Block block){
        switch (block.getType()){
            case GRASS_BLOCK:
                block.setType(Material.DIRT);
                break;
            case MYCELIUM:
                block.setType(Material.ROOTED_DIRT);
                break;
            case PODZOL:
            case DIRT:
                block.setType(Material.COARSE_DIRT);
                break;
            case COARSE_DIRT:
                block.setType(Material.DIRT_PATH);
                break;
            case SANDSTONE:
                block.setType(Material.SAND);
                break;
            case CRIMSON_NYLIUM:
            case WARPED_NYLIUM:
                block.setType(Material.NETHERRACK);
                break;
            case MAGMA_BLOCK:
                block.setType(Material.LAVA);
                break;
            default:
        }
    }
    private void blockBelowHandler(Block block, Player player){
        if (player.isSneaking() || player.isSwimming())
            return;
        if (!player.isSprinting() && random.nextInt(100) < getChance(player))
            globalSwitcher(block);
        if (player.isSprinting() && random.nextInt(100) < 12.5 + getChance(player))
            globalSwitcher(block);
    }

    private static modifierType getModifier(Player player){
        ItemStack boots = player.getInventory().getBoots();
        if (boots == null)
            return modifierType.NO_BOOTS;
        if (player.getVehicle() instanceof Horse)
            return modifierType.RIDING_HORSE;
        if (player.getVehicle() instanceof Boat)
            return modifierType.RIDING_BOAT;
        if (player.getVehicle() instanceof Pig)
            return modifierType.RIDING_PIG;
        switch (player.getInventory().getBoots().getType()){
            case IRON_BOOTS:
            case GOLDEN_BOOTS:
            case DIAMOND_BOOTS:
            case NETHERITE_BOOTS:
                break;
            case LEATHER_BOOTS:
                return modifierType.LEATHER_BOOTS;
            default:
                return modifierType.NO_BOOTS;
        }
        if (boots.containsEnchantment(Enchantment.PROTECTION_FALL))
            return modifierType.FEATHER_FALLING;
        return modifierType.HAS_BOOTS;
    }
    private enum modifierType{
        NO_BOOTS, LEATHER_BOOTS, HAS_BOOTS, FEATHER_FALLING, RIDING_HORSE, RIDING_BOAT, RIDING_PIG
    }
    public static int getChance(Player player){
        switch (getModifier(player)){
            case NO_BOOTS:
                return 3;
            case LEATHER_BOOTS:
                return 11;
            case HAS_BOOTS:
                return 20;
            case FEATHER_FALLING:
                return 10;
            case RIDING_HORSE:
                return 37;
            case RIDING_BOAT:
                return 99;
            case RIDING_PIG:
                return 30;
            default:
                return 0;
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("DesirePaths disabled!");
        // Plugin shutdown logic
    }
}
