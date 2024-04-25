package me.kermx.desirepaths.integrations;

import me.kermx.desirepaths.DesirePaths;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

public class GriefPreventionIntegration {
    private GriefPrevention griefPrevention;
    private DesirePaths plugin;

    public GriefPreventionIntegration(DesirePaths plugin){
        this.plugin = plugin;
        this.griefPrevention = GriefPrevention.instance;
    }

    public boolean checkLocation(Player player, Location location){
        if (griefPrevention == null) {
            griefPrevention = GriefPrevention.instance;
        }

        PlayerData playerData = griefPrevention.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = griefPrevention.dataStore.getClaimAt(location, false, null);

        if (claim != null) {
            if (playerData != null) {
                playerData.lastClaim = claim;
            }
            Supplier<String> noAccessReason = claim.checkPermission(player, ClaimPermission.Build, null);
            return noAccessReason == null;
        } else {
            return plugin.pathsInWildernessGriefPrevention;
        }
    }
}
