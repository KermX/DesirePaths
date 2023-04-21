package me.kermx.desirepaths.path;

import me.kermx.desirepaths.addons.Addon;
import me.kermx.desirepaths.managers.AddonManager;
import me.kermx.desirepaths.managers.Manager;
import me.kermx.desirepaths.utils.ConfigOptions;
import me.kermx.desirepaths.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PathHandler implements Runnable {

    private final Random random;

    public PathHandler() {
        this.random = new Random();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() != GameMode.SURVIVAL || player.hasPermission("desirepaths.exempt")) continue;

            int chance = PathModifierType.getModifier(player).getChance();
            int randomNum = random.nextInt(100);
            if (ConfigOptions.DISABLED_WORLDS.getValue(List.class).contains(player.getWorld().getName())) return;

            outer:
            for (Addon addon : Manager.get(AddonManager.class).getAddons()) {
                for (int i = 0; i < 2; i++) {
                    Block block = i == 0 ? player.getLocation().getBlock().getRelative(BlockFace.DOWN) : player.getLocation().getBlock();
                    if (!addon.isAllowed(player, block)) continue outer;

                    ConfigOptions configOptions = i == 1 ? ConfigOptions.SPRINTING_BLOCK_AT_FEET_CHANCE : ConfigOptions.SPRINTING_BLOCK_BELOW_CHANCE;
                    if (!player.isSprinting() && randomNum < chance) {
                        blockSwitcher(block, (i == 1 ? ConfigOptions.BLOCK_AT_FEET_SWITCHER_LIST : ConfigOptions.BLOCK_BELOW_SWITCHER_LIST).getValue(List.class));
                    }
                    if (player.isSprinting() && randomNum < chance + configOptions.getValue(Integer.class)) {
                        blockSwitcher(block, (i == 1 ? ConfigOptions.BLOCK_AT_FEET_SWITCHER_LIST : ConfigOptions.BLOCK_BELOW_SWITCHER_LIST).getValue(List.class));
                    }
                }
                return;
            }
        }
    }

    private void blockSwitcher(Block block, List<String> switcherConfig) {
        Material type = block.getType();
        Map<Material, Material> blockSwitcher = new HashMap<>();
        for (String switchCase : switcherConfig) {
            String[] parts = switchCase.split(":");
            Material sourceMaterial = Material.matchMaterial(parts[0]);
            Material targetMaterial = Material.matchMaterial(parts[1]);
            if (sourceMaterial != null && targetMaterial != null) {
                blockSwitcher.put(sourceMaterial, targetMaterial);
                continue;
            }
            Utils.sendMessage(null, "&6>>&4 Invalid block switch case in blockBelowModifications: " + switchCase);
        }
        Material targetMaterial = blockSwitcher.get(type);
        if (targetMaterial != null) block.setType(targetMaterial);
    }
}
