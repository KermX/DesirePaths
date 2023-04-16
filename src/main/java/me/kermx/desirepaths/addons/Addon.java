package me.kermx.desirepaths.addons;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class Addon {

    protected JavaPlugin plugin;

    public Addon(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void preLoad();

    public abstract void load();

    public abstract boolean isEnabled();
}
