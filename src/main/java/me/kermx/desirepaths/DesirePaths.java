package me.kermx.desirepaths;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import me.kermx.desirepaths.commands.ReloadCommand;
import me.kermx.desirepaths.path.PathModifierType;
import me.kermx.desirepaths.utils.ConfigOptions;
import me.kermx.desirepaths.utils.Utils;
import org.bukkit.*;
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

    private static DesirePaths instance;

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        Objects.requireNonNull(getCommand("desirepaths")).setExecutor(new ReloadCommand(this));

        // Plugin startup logic
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerHandler(player);
            }
        }, 0L, ConfigOptions.ATTEMPT_FREQUENCY.getValue(int.class));
    }

    private void playerHandler(Player player) {
        if (player.getGameMode() != GameMode.SURVIVAL || player.hasPermission("desirepaths.exempt"))
            return;
        int chance = PathModifierType.getModifier(player).getChance();
        int randomNum = random.nextInt(100);
        Bukkit.getScheduler().runTask(this,()-> blockBelowHandler(player.getLocation().getBlock().getRelative(BlockFace.DOWN), player, chance, randomNum));
        Bukkit.getScheduler().runTask(this,()-> blockAtFeetHandler(player.getLocation().getBlock(),player, chance, randomNum));
    }

    private void blockAtFeetHandler(Block block, Player player, int chance, int randomNum) {
        if (disabledWorlds.contains(player.getWorld().getName())){
            return;
        }
        if (worldGuardEnabled){
            if (worldGuardIntegration.checkFlag(player)) {
                return;
            }
        }
        if (!townyEnabled || !pathsOnlyWherePlayerCanBreak) {
            // Run towny not enabled
            if (!player.isSprinting() && randomNum < chance) {
                blockAtFeetSwitcher(block, blockAtFeetSwitcherConfig);
            }
            if (player.isSprinting() && randomNum < chance + sprintingBlockAtFeetChance) {
                blockAtFeetSwitcher(block, blockAtFeetSwitcherConfig);
            }
        } else {
            // Run if towny is enabled and canBuild is true
            boolean canBuild = PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.DESTROY);
            if (canBuild) {
                if (!player.isSprinting() && randomNum < chance) {
                    blockAtFeetSwitcher(block, blockAtFeetSwitcherConfig);
                }
                if (player.isSprinting() && randomNum < chance + sprintingBlockAtFeetChance) {
                    blockAtFeetSwitcher(block, blockAtFeetSwitcherConfig);
                }
            }
        }
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
                Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + ">>" + ChatColor.RED + " Invalid block switch case in blockAtFeetModifications: " + switchCase);
            }
        }
        Material targetMaterial = blockSwitcher.get(type);
        if (targetMaterial != null) {
            block.setType(targetMaterial);
        }
    }

    //Handle block below the player
    private void blockBelowHandler(Block block, Player player, int chance, int randomNumg) {
        if (disabledWorlds.contains(player.getWorld().getName())){
            return;
        }
        if (worldGuardEnabled){
            if (worldGuardIntegration.checkFlag(player)) {
                return;
            }
        }
        if (!townyEnabled || !pathsOnlyWherePlayerCanBreak) {
            // Run towny not enabled
            if (!player.isSprinting() && randomNum < chance) {
                blockBelowSwitcher(block, blockBelowSwitcherConfig);
            }
            if (player.isSprinting() && randomNum < chance + sprintingBlockBelowChance) {
                blockBelowSwitcher(block, blockBelowSwitcherConfig);
            }
        } else {
            // Run if towny is enabled and canBuild is true
            boolean canBuild = PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.DESTROY);
            if (canBuild) {
                if (!player.isSprinting() && randomNum < chance) {
                    blockBelowSwitcher(block, blockBelowSwitcherConfig);
                }
                if (player.isSprinting() && randomNum < chance + sprintingBlockBelowChance) {
                    blockBelowSwitcher(block, blockBelowSwitcherConfig);
                }
            }
        }
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
                Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + ">>" + ChatColor.RED + " Invalid block switch case in blockBelowModifications: " + switchCase);
            }
        }
        Material targetMaterial = blockSwitcher.get(type);
        if (targetMaterial != null) {
            block.setType(targetMaterial);
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        ConfigOptions.cachedValues.clear();
    }
    @Override
    public void onDisable() {
        Utils.sendMessage(null, "&6>>&4 DesirePaths Disabled");
        instance = null;
    }

    public static DesirePaths getInstance() {
        return instance;
    }
}
