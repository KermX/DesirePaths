package me.kermx.desirepaths.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kermx.desirepaths.DesirePaths;

public class DesirePathsCommand implements CommandExecutor {

    private final DesirePaths plugin;

    public DesirePathsCommand(DesirePaths plugin) {
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
        }
        sender.sendMessage(ChatColor.RED + "Incorrect Usage!");
        return false;
    }
}
