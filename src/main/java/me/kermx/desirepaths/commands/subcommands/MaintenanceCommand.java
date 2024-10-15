package me.kermx.desirepaths.commands.subcommands;

import me.kermx.desirepaths.DesirePaths;
import me.kermx.desirepaths.commands.DesirePathsSub;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MaintenanceCommand implements DesirePathsSub {

    private final DesirePaths plugin;

    public MaintenanceCommand(DesirePaths plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        boolean maintenanceMode = plugin.getToggleManager().getMaintenanceMode();
        plugin.getToggleManager().setMaintenanceMode(!maintenanceMode);
        String maintenanceStatus = maintenanceMode ? "off" : "on";
        sender.sendMessage(ChatColor.GREEN + "Maintenance mode toggled " + maintenanceStatus + "!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
