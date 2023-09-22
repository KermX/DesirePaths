package me.kermx.desirepaths.integrations;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WorldGuardIntegration {
    public static StateFlag DESIREPATHS_PATHS;

    public boolean checkFlag(Player player){
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        Location location = BukkitAdapter.adapt(player.getLocation());
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);
        StateFlag.State state = set.queryState(localPlayer, WorldGuardIntegration.DESIREPATHS_PATHS);
        return state != StateFlag.State.ALLOW;
    }




    public void registerFlag() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("desirepaths-paths", true);
            registry.register(flag);
            DESIREPATHS_PATHS = flag;
        } catch (
                FlagConflictException | IllegalStateException exception) {
            Flag<?> existing = registry.get("desirepaths-paths");
            if (existing instanceof StateFlag) {
                DESIREPATHS_PATHS = (StateFlag) existing;
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + ">>" + ChatColor.DARK_RED + " This is a WorldGuard error but I have no idea how or why this happened?!");
            }
        }
    }

    public void preloadWorldGuardIntegration(){
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null){
            registerFlag();
        }
    }

}

