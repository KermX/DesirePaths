package me.kermx.desirepaths.addons;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import me.kermx.desirepaths.utils.ConfigOptions;
import me.kermx.desirepaths.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TownyAddon extends Addon {

    public TownyAddon(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void preLoad() {

    }

    @Override
    public void load() {
        Utils.sendMessage(null, "&6>>&a DesirePaths-Towny integration successful");
    }

    @Override
    public boolean isEnabled() {
        return Bukkit.getPluginManager().getPlugin("Towny") != null;
    }

    @Override
    public boolean isAllowed(Player player, Block block) {
        return PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.DESTROY) && ConfigOptions.PATHS_WHERE_ONLY_PLAYER_CAN_BREAK.getValue(boolean.class);
    }
}
