package me.kermx.desirepaths.commands.subcommands;

import me.kermx.desirepaths.DesirePaths;
import me.kermx.desirepaths.commands.DesirePathsSub;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
* A class for Maintenance sub command.
*/
public class MaintenanceCommand implements DesirePathsSub {
    private final DesirePaths plugin;

    /**
    * Constructs this class on initialization.
    *
    * @param The plugin's instance
    */
    public MaintenanceCommand(final DesirePaths plugin) {
        this.plugin = plugin;
    }

    /**
    * If the method was triggered, we process it.
    *
    * @param sender The sender
    * @param args   Args of the command
    */
    @Override
    public void onCommand(final CommandSender sender, final String[] args) {
        final boolean maintenanceMode = plugin.getToggleManager().getMaintenanceMode();

        plugin.getToggleManager().setMaintenanceMode(!maintenanceMode);
        final String maintenanceStatus = maintenanceMode ? "off" : "on";

        sender.sendMessage(ChatColor.GREEN + "Maintenance mode toggled " + maintenanceStatus + "!");
    }

    /**
    * When tab complete is triggered, we process it.
    *
    * @param sender The sender
    * @param args   Args of the command
    * @return Nothing
    */
    @Override
    public List<String> onTabComplete(final CommandSender sender, final String[] args) {
        return List.of();
    }
}
