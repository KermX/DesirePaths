package me.kermx.desirepaths;

import me.kermx.desirepaths.managers.AddonManager;
import me.kermx.desirepaths.managers.CommandManager;
import me.kermx.desirepaths.managers.Manager;
import me.kermx.desirepaths.path.PathHandler;
import me.kermx.desirepaths.utils.ConfigOptions;
import me.kermx.desirepaths.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
        // handle the configs.
        this.saveDefaultConfig();

        // handle the managers
        Manager.get(AddonManager.class).load();
        CommandManager commandManager = new CommandManager(this);
        commandManager.setup();
        commandManager.load();

        // handle the timers.
        Bukkit.getScheduler().runTaskTimer(this, new PathHandler(), 0L, ConfigOptions.ATTEMPT_FREQUENCY.getValue(Integer.class));
    }

    @Override
    public void saveDefaultConfig() {
        super.saveDefaultConfig();
        this.reloadConfig();
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
