package me.kermx.desirepaths.commands.subcommands;

import me.kermx.desirepaths.DesirePaths;
import me.kermx.desirepaths.commands.DesirePathsSub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ToggleCommand implements DesirePathsSub {

    private final DesirePaths plugin;

    public ToggleCommand(DesirePaths plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                togglePlayer(sender);
            } else {
                toggleConsole(sender, args);
            }
        }
        return true;
    }

    private void togglePlayer(CommandSender sender) {
        Player player = (Player) sender;
        boolean newToggle = plugin.getToggleManager().toggle(player.getUniqueId());

        String toggleStatus = newToggle ? "on" : "off";
        player.sendMessage(ChatColor.GREEN + "DesirePaths toggled " + toggleStatus + "!");
     }

    private void toggleConsole(CommandSender sender, String[] args) {
        if (args.length == 2) {
            String playerName = args[1];
            Player player = Bukkit.getPlayer(playerName);

            if (player != null) {
                UUID playerId = player.getUniqueId();
                boolean newToggle = plugin.getToggleManager().toggle(playerId);

                String toggleStatus = newToggle ? "on" : "off";
                sender.sendMessage(ChatColor.GREEN + "DesirePaths toggled " + toggleStatus + "!");
            } else {
                sender.sendMessage(ChatColor.GREEN + "Unable to find online player with name: " + playerName);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            return List.of();
        } else {
            return toggleTabConsole();
        }
    }

    private List<String> toggleTabConsole() {
        List<String> onlinePlayers = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(player.getName());
        }

        return onlinePlayers;
    }
}
