package me.kermx.desirepaths.commands.subcommands;

import me.kermx.desirepaths.DesirePaths;
import me.kermx.desirepaths.commands.DesirePathsSub;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MaintenanceCommand implements DesirePathsSub {
    private final DesirePaths plugin;

    public MaintenanceCommand(final DesirePaths plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(final CommandSender sender, final String[] args) {
        final boolean maintenanceMode = plugin.getToggleManager().getMaintenanceMode();

        plugin.getToggleManager().setMaintenanceMode(!maintenanceMode);
        final String maintenanceStatus = maintenanceMode ? "off" : "on";

        sender.sendMessage(ChatColor.GREEN + "Maintenance mode toggled " + maintenanceStatus + "!");
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final String[] args) {
        return List.of();
    }
}
