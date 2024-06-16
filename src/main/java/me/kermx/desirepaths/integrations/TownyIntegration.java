package me.kermx.desirepaths.integrations;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import me.kermx.desirepaths.DesirePaths;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TownyIntegration {
    private DesirePaths plugin;

    public TownyIntegration(DesirePaths plugin){
        this.plugin = plugin;
    }

    public boolean checkLocation(Player player, Location location){
        if (TownyAPI.getInstance().isTownyWorld(location.getWorld())){

            if (TownyAPI.getInstance().isWilderness(location)){
                return plugin.pathsInWildernessTowny;

            } else {

                if (plugin.noPathsInAnyTown){
                    return false;
                }

                if (plugin.pathsOnlyWherePlayerCanBreakTowny) {
                    Block block = location.getBlock();
                    return PlayerCacheUtil.getCachePermission(player, location, block.getType(),
                            TownyPermission.ActionType.DESTROY);
                } else {
                    return true;
                }
            }
        } else {
            return true;
        }
    }
}
