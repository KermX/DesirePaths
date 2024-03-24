package me.kermx.desirepaths.integrations;

import me.kermx.desirepaths.DesirePaths;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CoreProtectIntegration {
    private final DesirePaths plugin;
    private CoreProtectAPI coreProtectAPI;

    public CoreProtectIntegration(DesirePaths plugin){
        this.plugin = plugin;
        coreProtectAPI = getCoreProtectAPI();
    }

    private CoreProtectAPI getCoreProtectAPI(){
        Plugin coreProtectPlugin = Bukkit.getPluginManager().getPlugin("CoreProtect");
        if (!(coreProtectPlugin instanceof CoreProtect)){
            return null;
        }
        CoreProtectAPI CoreProtectAPI = (((CoreProtect) coreProtectPlugin).getAPI());
        if (!CoreProtectAPI.isEnabled()){
            return null;
        }
        if (CoreProtectAPI.APIVersion() < 9){
            return null;
        }
        return CoreProtectAPI;
    }

    public CoreProtectAPI getAPI(){
        return this.coreProtectAPI;
    }

    public void logPathChangesToCoreProtectRemoval(Player player, Location location, Material material, BlockData blockData){
        if (coreProtectAPI != null){
            coreProtectAPI.logRemoval(player.getName(), location, material, blockData);
        }
    }
    public void logPathChangesToCoreProtectPlacement(Player player, Location location, Material material, BlockData blockData){
        if (coreProtectAPI != null){
            coreProtectAPI.logPlacement(player.getName(), location, material, blockData);
        }
    }
}
