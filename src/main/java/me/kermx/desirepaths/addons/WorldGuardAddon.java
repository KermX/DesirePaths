package me.kermx.desirepaths.addons;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import me.kermx.desirepaths.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldGuardAddon extends Addon {

    private static StateFlag DESIREPATHS_PATHS;
    private WorldGuardPlugin worldGuardPlugin;
    private WorldGuard worldGuard;

    public WorldGuardAddon(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void preLoad() {
        worldGuard = WorldGuard.getInstance();
        FlagRegistry registry = worldGuard.getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("desirepaths-paths", true);
            if (registry.get(flag.getName()) != null) {
                Flag<?> existing = registry.get(flag.getName());
                if (existing instanceof StateFlag) {
                    DESIREPATHS_PATHS = (StateFlag) existing;
                    return;
                }
                Utils.sendMessage(null, "&6>>&4This is a WorldGuard error, another plugin has registered the same flag \"desirepaths-paths\" and it is not a StateFlag.");
                return; // flag was already registered?
            }
            registry.register(DESIREPATHS_PATHS = flag);
        } catch (FlagConflictException | IllegalStateException ignored) {} // this error will not happen as we check beforehand.
    }

    @Override
    public void load() {
        worldGuardPlugin = WorldGuardPlugin.inst();
        Utils.sendMessage(null, "&6>>&a DesirePaths-WorldGuard integration successful");
    }

    @Override
    public boolean isEnabled() {
        return Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    }

    @Override
    public boolean isAllowed(Player player, Block block) {
        return this.worldGuard
                .getPlatform()
                .getRegionContainer()
                .createQuery()
                .getApplicableRegions(BukkitAdapter.adapt(player.getLocation()))
                .queryState(this.worldGuardPlugin.wrapPlayer(player), DESIREPATHS_PATHS) == StateFlag.State.ALLOW;
    }
}
