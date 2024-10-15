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

    public ToggleCommand(final DesirePaths plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(final CommandSender sender, final String[] args) {
        if (sender instanceof Player) {
            togglePlayer(sender);
        } else {
            toggleConsole(sender, args);
        }
    }

    /**
     * To process if the sender is player
     *
     * @param sender The player
     */
    private void togglePlayer(final CommandSender sender) {
        final Player player = (Player) sender;
        final UUID playerId = player.getUniqueId();

        final boolean newToggle = plugin.getToggleManager().toggle(playerId);
        final String toggleStatus = newToggle ? "on" : "off";

        player.sendMessage("IsToggleMode? " + toggleStatus);
        player.sendMessage(ChatColor.GREEN + "DesirePaths toggled " + toggleStatus + "!");
     }

    /**
     * To process if the sender is console
     *
     * @param sender The console
     * @param args   Arguments for the sub command (we expect player)
     */
    private void toggleConsole(final CommandSender sender, final String[] args) {
        if (args.length >= 2) {
            final String playerName = args[1];
            final Player player = Bukkit.getPlayer(playerName);

            if (player != null) {
                final UUID playerId = player.getUniqueId();

                final boolean newToggle = plugin.getToggleManager().toggle(playerId);
                final String toggleStatus = newToggle ? "on" : "off";

                sender.sendMessage(ChatColor.GREEN + "DesirePaths toggled " + toggleStatus + "!");
            } else {
                sender.sendMessage(ChatColor.GREEN + "Unable to find online player with name: " + playerName);
            }
        } else {
            sender.sendMessage(ChatColor.GREEN + "Usage: /desirepaths toggle [player]");
        }
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final String[] args) {
        if (sender instanceof Player) {
            return List.of();
        } else {
            return toggleTabConsole();
        }
    }

    /**
     * If the tab complete event is coming from console,
     * we return players list.
     *
     * @return List of players
     */
    private List<String> toggleTabConsole() {
        final List<String> onlinePlayers = new ArrayList<>();

        for (final Player player : Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(player.getName());
        }

        return onlinePlayers;
    }
}
