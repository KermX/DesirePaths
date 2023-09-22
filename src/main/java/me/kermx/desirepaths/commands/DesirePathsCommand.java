package me.kermx.desirepaths.commands;

import me.kermx.desirepaths.DesirePaths;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                    boolean currentToggle = plugin.getToggleManager().getToggle(player.getUniqueId());
                    boolean newToggle = !currentToggle;

                    plugin.getToggleManager().setToggle(player.getUniqueId(), newToggle);

                    String toggleStatus = newToggle ? "on" : "off";
                    player.sendMessage(ChatColor.GREEN + "DesirePaths toggled " + toggleStatus + "!");
                    return true;
                }
            }
        }
        sender.sendMessage(ChatColor.RED + "Incorrect Usage!");
        return false;
    }
}
