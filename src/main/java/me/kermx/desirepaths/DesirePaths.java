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
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

import static java.lang.Math.abs;

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
        if (random.nextInt(100) < abs(getChance(player)) - 3 && block.getType() == Material.FARMLAND)
            block.setType(Material.MUD);
        if (random.nextInt(100) < abs(getChance(player)) + 30 || player.isSprinting())
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
                    return;
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
                return;
        }
    }
    private void blockBelowHandler(Block block, Player player){
        if (player.isSneaking() || player.isSwimming())
            return;
        if (random.nextInt(100) < abs(getChance(player)))
            globalSwitcher(block);
    }

    private static modifierType getModifier(Player player){
        ItemStack boots = player.getInventory().getBoots();
        if (boots == null)
            return modifierType.NO_BOOTS;
        if (player.hasPotionEffect(PotionEffectType.SLOW_FALLING))
            return modifierType.SLOW_FALLING;
        if (player.isSprinting())
            return modifierType.IS_SPRINTING;
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
        NO_BOOTS, LEATHER_BOOTS, HAS_BOOTS, FEATHER_FALLING, SLOW_FALLING, RIDING_HORSE, RIDING_BOAT, RIDING_PIG, IS_SPRINTING
    }
    public static float getChance(Player player){
        switch (getModifier(player)){
            case NO_BOOTS:
                return 3.0F;
            case LEATHER_BOOTS:
                return 11.0F;
            case HAS_BOOTS:
                return 20.0F;
            case FEATHER_FALLING:
                return -10.0F;
            case SLOW_FALLING:
                return -5.5F;
            case RIDING_HORSE:
                return 37.5F;
            case RIDING_BOAT:
                return 99.0F;
            case RIDING_PIG:
                return 30.0F;
            case IS_SPRINTING:
                return 12.5F;
            default:
                return 0.0F;
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("DesirePaths disabled!");
        // Plugin shutdown logic
    }
}
