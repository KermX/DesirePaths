/*
package me.kermx.desirepaths.managers;

import me.kermx.desirepaths.DesirePaths;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class SpeedBoostHandler implements Listener {
    private final DesirePaths plugin;
    public SpeedBoostHandler(DesirePaths plugin){
        this.plugin=plugin;
    }

    @EventHandler
    public void onPlayerMoveOnPath(PlayerMoveEvent event) {
        if (!plugin.speedBoostEnabled) {
            return;
        }
        if (event.getFrom().getZ() != event.getTo().getZ() || event.getFrom().getX() != event.getTo().getX()){
            Player player = event.getPlayer();
            Material blockTypeBelowFeet = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
            Material blockTypeAtFeet = player.getLocation().getBlock().getType();
            if (plugin.speedBoostBlocks.contains(blockTypeBelowFeet.toString()) || plugin.speedBoostBlocks.contains(blockTypeAtFeet.toString())) {
                player.setWalkSpeed(plugin.boostedWalkSpeedConverted);
            } else {
                player.setWalkSpeed(0.2f);
            }
        }
    }
} */
