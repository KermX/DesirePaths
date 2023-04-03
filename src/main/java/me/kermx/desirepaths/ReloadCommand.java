package me.kermx.desirepaths;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final DesirePaths plugin;

    public ReloadCommand(DesirePaths plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("desirepaths")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                plugin.loadConfig();
                sender.sendMessage(ChatColor.GREEN + "DesirePaths configuration reloaded!");
                return true;
            }
        }
        return false;
    }
}
