package me.kermx.desirepaths.addons;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlaceholderAddon extends Addon{

    public PlaceholderAddon(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void preLoad() {

    }

    @Override
    public void load() {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAllowed(Player player, Block block) {
        return true;
    }
}
