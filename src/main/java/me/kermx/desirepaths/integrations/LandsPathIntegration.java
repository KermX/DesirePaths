package me.kermx.desirepaths.integrations;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.enums.FlagTarget;
import me.angeschossen.lands.api.flags.enums.RoleFlagCategory;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.LandWorld;
import me.kermx.desirepaths.DesirePaths;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LandsPathIntegration {

    private final DesirePaths plugin;
    private LandsIntegration landsIntegration;
    private RoleFlag desirePathsFlag;

    public LandsPathIntegration(DesirePaths plugin) {
        this.plugin = plugin;
        this.landsIntegration = LandsIntegration.of(plugin);
    }


    public boolean checkFlag(Player player){
        LandWorld world = landsIntegration.getWorld(player.getWorld());
        Location location = player.getLocation();
        if (world != null){ //Lands is enabled
            return !world.hasRoleFlag(player.getUniqueId(), location, desirePathsFlag);
        } else {
            return false; // the world is not effected by lands
        }
    }

    public void registerFlags(){
        RoleFlag roleFlag = RoleFlag.of(landsIntegration, FlagTarget.PLAYER, RoleFlagCategory.ACTION, "DESIRE_PATHS");

        roleFlag.setDisplayName(plugin.flagDisplayName).setIcon(new ItemStack(Material.matchMaterial(plugin.flagDisplayMaterial)))
                .setDescription(plugin.flagDisplayDescription).setDisplay(plugin.displayFlag).setDefaultState(plugin.defaultFlagState);

        this.desirePathsFlag = roleFlag;
    }

    public void loadLandsIntegration(){
        registerFlags();
    }
}
