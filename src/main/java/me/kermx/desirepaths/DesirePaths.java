package me.kermx.desirepaths;

import me.kermx.desirepaths.commands.ReloadCommand;
import me.kermx.desirepaths.managers.AddonManager;
import me.kermx.desirepaths.managers.Manager;
import me.kermx.desirepaths.path.PathHandler;
import me.kermx.desirepaths.utils.ConfigOptions;
import me.kermx.desirepaths.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class DesirePaths extends JavaPlugin {

    private static DesirePaths instance;

    @Override
    public void onLoad() {
        instance = this;
        AddonManager addonManager = new AddonManager(this);
        addonManager.setup();
        addonManager.preload();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        Objects.requireNonNull(getCommand("desirepaths")).setExecutor(new ReloadCommand(this));

        PathHandler pathHandler = new PathHandler(this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, pathHandler, 0L, ConfigOptions.ATTEMPT_FREQUENCY.getValue(int.class));
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        ConfigOptions.cachedValues.clear();
    }
    @Override
    public void onDisable() {
        Utils.sendMessage(null, "&6>>&4 DesirePaths Disabled");
        Manager.instances.clear();
        instance = null;
    }

    public static DesirePaths getInstance() {
        return instance;
    }
}
