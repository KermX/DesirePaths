package me.kermx.desirepaths.addons;

import me.kermx.desirepaths.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class TownyAddon extends Addon {

    public TownyAddon(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void preLoad() {

    }

    @Override
    public void load() {
        Utils.sendMessage(null, "&6>>&a DesirePaths-Towny integration successful");
    }

    @Override
    public boolean isEnabled() {
        return Bukkit.getPluginManager().getPlugin("Towny") != null;
    }
}
