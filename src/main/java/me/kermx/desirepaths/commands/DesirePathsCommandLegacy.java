package me.kermx.desirepaths.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.kermx.desirepaths.DesirePaths;

public class DesirePathsCommandLegacy implements CommandExecutor, TabCompleter {

    private final DesirePaths plugin;

    public DesirePathsCommandLegacy(DesirePaths plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("desirepaths")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("desirepaths.reload")) {
                    plugin.reloadConfig();
                    plugin.loadConfig();
                    sender.sendMessage(ChatColor.GREEN + "DesirePaths configuration reloaded!");
                    return true;
                }
            }


            if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can run this command!");
                    return true;
                }
                if (sender.hasPermission("desirepaths.toggle")) {
                    Player player = (Player) sender;
                    boolean newToggle = plugin.getToggleManager().toggle(player.getUniqueId());

                    String toggleStatus = newToggle ? "on" : "off";
                    player.sendMessage(ChatColor.GREEN + "DesirePaths toggled " + toggleStatus + "!");
                    return true;
                }
            }

            // Handle console command to toggle players by name
            if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
                if (!(sender instanceof Player)) {
                    String playerName = args[1];
                    Player togglePlayer = Bukkit.getPlayer(playerName);
                    if (togglePlayer != null) {
                        UUID playerId = togglePlayer.getUniqueId();
                        boolean newToggle = plugin.getToggleManager().toggle(playerId);

                        String toggleStatus = newToggle ? "on" : "off";
                        sender.sendMessage(ChatColor.GREEN + "DesirePaths toggled " + toggleStatus + "!");
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Unable to find online player with name: " + playerName);
                    }
                    return true;
                }
            }
            // Handle maintenance command to toggle paths for all players
            if (args.length == 1 && args[0].equalsIgnoreCase("maintenance")){
                if (sender.hasPermission("desirepaths.maintenance")){
                    boolean maintenanceMode = plugin.getToggleManager().getMaintenanceMode();
                    plugin.getToggleManager().setMaintenanceMode(!maintenanceMode);
                    String maintenanceStatus = maintenanceMode ? "off" : "on";
                    sender.sendMessage(ChatColor.GREEN + "Maintenance mode toggled " + maintenanceStatus + "!");
                    return true;
                }
            }
        }
        sender.sendMessage(ChatColor.RED + "Incorrect Usage! Try: /desirepaths <reload|toggle> [player]");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("desirepaths")) {
            if (args.length == 1) {
                // Autocomplete for the first argument if the sender has permission
                if (sender.hasPermission("desirepaths.reload")) {
                    completions.add("reload");
                }
                if (sender.hasPermission("desirepaths.toggle")) {
                    completions.add("toggle");
                }
                if (sender.hasPermission("desirepaths.maintenance")){
                    completions.add("maintenance");
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
                // Autocomplete second argument when the first argument is "toggle"
                if (sender.hasPermission("desirepaths.toggle")) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        completions.add(onlinePlayer.getName());
                    }
                }
            }
        }
        return completions;
    }
}
