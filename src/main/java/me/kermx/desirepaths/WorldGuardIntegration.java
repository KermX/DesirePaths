package me.kermx.desirepaths;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class WorldGuardIntegration {
    public static StateFlag DESIREPATHS_PREVENT_PATHS;


    public void registerFlag() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("desirepaths-prevent-paths", false);
            registry.register(flag);
            DESIREPATHS_PREVENT_PATHS = flag;
        } catch (
                FlagConflictException | IllegalStateException exception) {
            Flag<?> existing = registry.get("desirepaths-prevent-paths");
            if (existing instanceof StateFlag) {
                DESIREPATHS_PREVENT_PATHS = (StateFlag) existing;
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
