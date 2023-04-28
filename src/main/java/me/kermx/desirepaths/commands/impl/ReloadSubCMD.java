package me.kermx.desirepaths.commands.impl;

import me.kermx.desirepaths.DesirePaths;
import me.kermx.desirepaths.commands.DesireCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCMD extends DesireCommand {

    private final DesirePaths plugin;

    public ReloadSubCMD(DesirePaths plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "desirepaths.reload";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "DesirePaths configuration reloaded!");
    }
}
