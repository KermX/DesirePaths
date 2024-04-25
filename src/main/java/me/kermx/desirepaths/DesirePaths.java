package me.kermx.desirepaths;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import me.kermx.desirepaths.commands.DesirePathsCommand;
import me.kermx.desirepaths.integrations.*;
import me.kermx.desirepaths.managers.ToggleManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// new features:
// speed boost when walking on paths
// papi placeholders, %desirepaths_toggle_status%, %desirepaths_maintenance_status%
// weather chance modifiers
// different block switch based on weather
// different block switch and/or biome chance modifiers
// itemsadder blocks?


public final class DesirePaths extends JavaPlugin implements Listener {
    private List<String> disabledWorlds;
    private boolean movementCheckEnabled;
    private int noBootsChance;
    private int leatherBootsChance;
    private int hasBootsChance;
    private int featherFallingChance;
    private int ridingHorseChance;
    private int ridingBoatChance;
    private int ridingPigChance;
    private int sprintingBlockBelowChance;
    private int sprintingBlockAtFeetChance;
    private int crouchingBlockBelowChance;
    private int crouchingBlockAtFeetChance;
    private int attemptFrequency;
    private List<String> blockBelowSwitcherConfig;
    private List<String> blockAtFeetSwitcherConfig;
    public boolean pathsOnlyWherePlayerCanBreakTowny;
    public boolean pathsInWildernessTowny;
    public boolean pathsOnlyWherePlayerCanBreakGriefPrevention;
    public boolean pathsInWildernessGriefPrevention;
    public boolean displayFlag;
    public String flagDisplayName;
    public String flagDisplayDescription;
    public String flagDisplayMaterial;
    public boolean defaultFlagState;
    public boolean logPathsToCoreProtect;
    private boolean enableInCreativeMode;

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private enum modifierType {
        NO_BOOTS, LEATHER_BOOTS, HAS_BOOTS, FEATHER_FALLING, RIDING_HORSE, RIDING_BOAT, RIDING_PIG
    }

    private TownyIntegration townyIntegration;
    private WorldGuardIntegration worldGuardIntegration;
    private LandsPathIntegration landsPathIntegration;
    private GriefPreventionIntegration griefPreventionIntegration;
    private CoreProtectIntegration coreProtectIntegration;

    public boolean townyEnabled;
    public boolean worldGuardEnabled;
    public boolean landsEnabled;
    public boolean griefPreventionEnabled;
    public boolean coreProtectEnabled;

    private ToggleManager toggleManager;
    DesirePathsCommand desirePathsCommand = new DesirePathsCommand(this);

    @Override
    public void onLoad() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        loadConfig();

        if (Bukkit.getPluginManager().getPlugin("Towny") != null){
            try {
                townyIntegration = new TownyIntegration(this);
            } catch (NoClassDefFoundError ignored){
            }
        }
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            try {
                worldGuardIntegration = new WorldGuardIntegration();
                worldGuardIntegration.preloadWorldGuardIntegration();
            } catch (NoClassDefFoundError ignored) {
            }
        }
        if (Bukkit.getPluginManager().getPlugin("Lands") != null){
            try {
                landsPathIntegration = new LandsPathIntegration(this);
                landsPathIntegration.loadLandsIntegration();
            } catch (NoClassDefFoundError ignored){
            }
        }
        if (Bukkit.getPluginManager().getPlugin("GriefPrevention") != null){
            try {
                griefPreventionIntegration = new GriefPreventionIntegration(this);
            } catch (NoClassDefFoundError ignored){
            }
        }
    }

    // PlayerMoveEvent related stuff
    private boolean playerHasMoved = false;

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        if (!movementCheckEnabled) {
            return; // Do nothing if movementCheckEnabled is false
        }

        double deltaX = Math.abs(event.getFrom().getX() - event.getTo().getX());
        double deltaZ = Math.abs(event.getFrom().getZ() - event.getTo().getZ());

        playerHasMoved = deltaX > 0.01 || deltaZ > 0.01;
    }

    @Override
    public void onEnable() {

        // initialize reload & toggle command
        Objects.requireNonNull(getCommand("desirepaths")).setExecutor(desirePathsCommand);
        Objects.requireNonNull(getCommand("desirepaths")).setTabCompleter(desirePathsCommand);

        // initialize togglemanager
        toggleManager = new ToggleManager(this);

        // initialize coreprotect integration
        if (Bukkit.getPluginManager().getPlugin("CoreProtect") != null)
            try {
                coreProtectIntegration = new CoreProtectIntegration(this);
            } catch (NoClassDefFoundError ignored){
            }

        // Plugin startup logic
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (movementCheckEnabled && playerHasMoved) {
                    playerHandler(player, noBootsChance, leatherBootsChance, hasBootsChance, featherFallingChance,
                            ridingHorseChance, ridingBoatChance, ridingPigChance, sprintingBlockBelowChance,
                            sprintingBlockAtFeetChance, crouchingBlockBelowChance, crouchingBlockAtFeetChance,
                            blockAtFeetSwitcherConfig, blockBelowSwitcherConfig);
                } else if (movementCheckEnabled) {
                    return;
                } else {
                    playerHandler(player, noBootsChance, leatherBootsChance, hasBootsChance, featherFallingChance,
                            ridingHorseChance, ridingBoatChance, ridingPigChance, sprintingBlockBelowChance,
                            sprintingBlockAtFeetChance, crouchingBlockBelowChance, crouchingBlockAtFeetChance,
                            blockAtFeetSwitcherConfig, blockBelowSwitcherConfig);
                }
            }
        }, 0L, attemptFrequency);

        getServer().getPluginManager().registerEvents(this, this);

        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.GOLD + ">>" + ChatColor.GREEN + " DesirePaths " + getDescription().getVersion() + " enabled successfully");

        townyEnabled = Bukkit.getPluginManager().isPluginEnabled("Towny");
        if (townyEnabled) {
            Bukkit.getConsoleSender()
                    .sendMessage(ChatColor.GOLD + ">>" + ChatColor.GREEN + " DesirePaths-Towny integration successful");
        }

        worldGuardEnabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
        if (worldGuardEnabled) {
            Bukkit.getConsoleSender().sendMessage(
                    ChatColor.GOLD + ">>" + ChatColor.GREEN + " DesirePaths-WorldGuard integration successful");
        }

        landsEnabled = Bukkit.getPluginManager().isPluginEnabled("Lands");
        if (landsEnabled) {
            Bukkit.getConsoleSender().sendMessage(
                    ChatColor.GOLD + ">>" + ChatColor.GREEN + " DesirePaths-Lands integration successful");
        }

        griefPreventionEnabled = Bukkit.getPluginManager().isPluginEnabled("GriefPrevention");
        if (griefPreventionEnabled){
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + ">>" + ChatColor.GREEN + " DesirePaths-GriefPrevention integration successful");
        }

        coreProtectEnabled = Bukkit.getPluginManager().isPluginEnabled("CoreProtect");
        if (coreProtectEnabled && coreProtectIntegration.getAPI() != null){
                Bukkit.getConsoleSender()
                        .sendMessage(ChatColor.GOLD + ">>" + ChatColor.GREEN + " DesirePaths-CoreProtect integration successful");
        }
    }

    private void playerHandler(Player player, int noBootsChance, int leatherBootsChance, int hasBootsChance,
                               int featherFallingChance, int ridingHorseChance, int ridingBoatChance, int ridingPigChance,
                               int sprintingBlockBelowChance, int sprintingBlockAtFeetChance, int crouchingBlockBelowChance,
                               int crouchingBlockAtFeetChance, List<String> blockAtFeetSwitcherConfig, List<String> blockBelowSwitcherConfig) {
        if (player.getGameMode() != GameMode.SURVIVAL && !enableInCreativeMode) {
            return;
        }
        boolean pathsToggledOff = !getToggleManager().getToggle(player.getUniqueId());
        if (pathsToggledOff) {
            return;
        }
        int chance = getChance(player, noBootsChance, leatherBootsChance, hasBootsChance, featherFallingChance,
                ridingHorseChance, ridingBoatChance, ridingPigChance);
        int randomNum = random.nextInt(100);
        Bukkit.getScheduler().runTask(this,
                () -> blockHandler(player.getLocation().getBlock().getRelative(BlockFace.DOWN), player, chance,
                        randomNum, sprintingBlockBelowChance, crouchingBlockBelowChance, blockBelowSwitcherConfig));
        Bukkit.getScheduler().runTask(this,
                () -> blockHandler(player.getLocation().getBlock(), player, chance,
                randomNum, sprintingBlockAtFeetChance, crouchingBlockAtFeetChance, blockAtFeetSwitcherConfig));
    }

    public static int getChance(Player player, int noBootsChance, int leatherBootsChance, int hasBootsChance,
            int featherFallingChance, int ridingHorseChance, int ridingBoatChance, int ridingPigChance) {
        return switch (getModifier(player)) {
        case RIDING_HORSE -> ridingHorseChance;
        case RIDING_BOAT -> ridingBoatChance;
        case RIDING_PIG -> ridingPigChance;
        case FEATHER_FALLING -> featherFallingChance;
        case HAS_BOOTS -> hasBootsChance;
        case LEATHER_BOOTS -> leatherBootsChance;
        case NO_BOOTS -> noBootsChance;
        };
    }

    // determine modifier to use for chance
    private static modifierType getModifier(Player player) {
        if (player.getVehicle() instanceof AbstractHorse)
            return modifierType.RIDING_HORSE;
        if (player.getVehicle() instanceof Boat)
            return modifierType.RIDING_BOAT;
        if (player.getVehicle() instanceof Pig)
            return modifierType.RIDING_PIG;
        ItemStack boots = player.getInventory().getBoots();
        if (boots == null)
            return modifierType.NO_BOOTS;
        Material bootMaterial = boots.getType();
        Set<Material> bootMaterials = EnumSet.of(Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS,
                Material.NETHERITE_BOOTS, Material.LEATHER_BOOTS);
        Optional<Enchantment> featherFallingEnchantment = boots.getEnchantments().keySet().stream()
                .filter(Enchantment.PROTECTION_FALL::equals).findFirst();
        if (bootMaterials.contains(bootMaterial)) {
            if (featherFallingEnchantment.isPresent()) {
                return modifierType.FEATHER_FALLING;
            } else {
                return modifierType.HAS_BOOTS;
            }
        } else if (bootMaterial == Material.LEATHER_BOOTS) {
            return modifierType.LEATHER_BOOTS;
        } else {
            return modifierType.NO_BOOTS;
        }
    }

    // Handle block at the players feet
    private void blockHandler(Block block, Player player, int chance, int randomNum, int sprintingChance,
                              int crouchingChance, List<String> switcherConfig) {
        if (toggleManager.getMaintenanceMode()){
            return;
        }
        if (disabledWorlds.contains(player.getWorld().getName())) {
            return;
        }
        if (worldGuardEnabled) {
            if (worldGuardIntegration.checkFlag(player)) {
                return;
            }
        }
        if (landsEnabled) {
            if (landsPathIntegration.checkFlag(player)){
                return;
            }
        }
        if (griefPreventionEnabled){
            if (!griefPreventionIntegration.checkLocation(player,player.getLocation())){
                return;
            }
        }
        if (townyEnabled) {
            if (!townyIntegration.checkLocation(player,player.getLocation())){
                return;
            }
        }

        if (!player.isSprinting() && !player.isSneaking() && randomNum < chance) {
            blockSwitcher(block, switcherConfig, player);
        }
        if (player.isSprinting() && randomNum < chance + sprintingChance) {
            blockSwitcher(block, switcherConfig, player);
        }
        if (player.isSneaking() && randomNum < chance + crouchingChance){
            blockSwitcher(block, switcherConfig, player);
        }
    }

    private void blockSwitcher(Block block, List<String> switcherConfig, Player player) {
        Material type = block.getType();
        Map<Material, Material> blockSwitcher = new HashMap<>();
        for (String switchCase : switcherConfig) {
            String[] parts = switchCase.split(":");
            Material sourceMaterial = Material.matchMaterial(parts[0]);
            Material targetMaterial = Material.matchMaterial(parts[1]);
            if (sourceMaterial != null && targetMaterial != null) {
                blockSwitcher.put(sourceMaterial, targetMaterial);
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + ">>" + ChatColor.RED
                        + " Invalid block switch case in blockModifications: " + switchCase);
            }
        }
        Material targetMaterial = blockSwitcher.get(type);
        if (targetMaterial != null) {
            block.setType(targetMaterial);
            //coreprotect logging
            if (block.getType() == targetMaterial){
                if (coreProtectEnabled && logPathsToCoreProtect){
                    coreProtectIntegration.logPathChangesToCoreProtectRemoval(player, block.getLocation(), type, block.getBlockData());
                    coreProtectIntegration.logPathChangesToCoreProtectPlacement(player, block.getLocation(), targetMaterial, block.getBlockData());
                }
            }
        }
    }

    public void loadConfig() {
        // initial config attemptFrequency value
        attemptFrequency = getConfig().getInt("attemptFrequency");
        // initial config disabledWorlds list
        disabledWorlds = getConfig().getStringList("disabledWorlds");
        // initial config movementCheckEnabled boolean
        movementCheckEnabled = getConfig().getBoolean("movementCheckEnabled");
        // initial config chanceModifier values
        noBootsChance = getConfig().getInt("chanceModifiers.NO_BOOTS");
        leatherBootsChance = getConfig().getInt("chanceModifiers.LEATHER_BOOTS");
        hasBootsChance = getConfig().getInt("chanceModifiers.HAS_BOOTS");
        featherFallingChance = getConfig().getInt("chanceModifiers.FEATHER_FALLING");
        ridingHorseChance = getConfig().getInt("chanceModifiers.RIDING_HORSE");
        ridingBoatChance = getConfig().getInt("chanceModifiers.RIDING_BOAT");
        ridingPigChance = getConfig().getInt("chanceModifiers.RIDING_PIG");
        sprintingBlockBelowChance = getConfig().getInt("chanceModifiers.SPRINTING_BLOCK_BELOW");
        sprintingBlockAtFeetChance = getConfig().getInt("chanceModifiers.SPRINTING_BLOCK_AT_FEET");
        crouchingBlockBelowChance = getConfig().getInt("chanceModifiers.CROUCHING_BLOCK_BELOW");
        crouchingBlockAtFeetChance = getConfig().getInt("chanceModifiers.CROUCHING_BLOCK_AT_FEET");
        // initial config blockModifications lists
        blockBelowSwitcherConfig = getConfig().getStringList("blockModifications.blockBelowModifications");
        blockAtFeetSwitcherConfig = getConfig().getStringList("blockModifications.blockAtFeetModifications");
        // initial config townyModifiers booleans
        pathsOnlyWherePlayerCanBreakTowny = getConfig().getBoolean("townyModifiers.pathsOnlyWherePlayerCanBreak");
        pathsInWildernessTowny = getConfig().getBoolean("townyModifiers.pathsInWilderness");
        // initial config griefPreventionIntegration booleans
        pathsOnlyWherePlayerCanBreakGriefPrevention = getConfig().getBoolean("griefPreventionIntegration.pathsOnlyWherePlayerCanBreak");
        pathsInWildernessGriefPrevention = getConfig().getBoolean("griefPreventionIntegration.pathsInWilderness");
        // initial config landsIntegration settings
        displayFlag = getConfig().getBoolean("landsIntegrations.displayFlag");
        flagDisplayName = getConfig().getString("landsIntegrations.flagDisplayName");
        flagDisplayDescription = getConfig().getString("landsIntegrations.flagDisplayDescription");
        flagDisplayMaterial = getConfig().getString("landsIntegrations.flagDisplayMaterial");
        defaultFlagState = getConfig().getBoolean("landsIntegrations.defaultFlagState");
        // initial config coreProtectIntegration booleans
        logPathsToCoreProtect = getConfig().getBoolean("coreProtectIntegrations.logPathsToCoreProtect");

        enableInCreativeMode = getConfig().getBoolean("enableInCreativeMode");
    }

    public ToggleManager getToggleManager() {
        return toggleManager;
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + ">>" + ChatColor.RED + " DesirePaths Disabled");
    }
}
