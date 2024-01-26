package me.kermx.desirepaths;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;

import me.kermx.desirepaths.commands.DesirePathsCommand;
import me.kermx.desirepaths.integrations.WorldGuardIntegration;
//import me.kermx.desirepaths.managers.SpeedBoostHandler;
import me.kermx.desirepaths.managers.ToggleManager;

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
    private int attemptFrequency;
    private List<String> blockBelowSwitcherConfig;
    private List<String> blockAtFeetSwitcherConfig;
    private boolean pathsOnlyWherePlayerCanBreak;
    private boolean enableInCreativeMode;

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private enum modifierType {
        NO_BOOTS, LEATHER_BOOTS, HAS_BOOTS, FEATHER_FALLING, RIDING_HORSE, RIDING_BOAT, RIDING_PIG
    }

    private WorldGuardIntegration worldGuardIntegration;

    private boolean townyEnabled;
    public boolean worldGuardEnabled;

    private ToggleManager toggleManager;
    DesirePathsCommand desirePathsCommand = new DesirePathsCommand(this);

    @Override
    public void onLoad() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            try {
                worldGuardIntegration = new WorldGuardIntegration();
                worldGuardIntegration.preloadWorldGuardIntegration();
            } catch (NoClassDefFoundError ignored) {
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

        playerHasMoved = deltaX > 0.1 || deltaZ > 0.1;
    }

    @Override
    public void onEnable() {
        // Load config
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        loadConfig();

        // initialize reload & toggle command
        Objects.requireNonNull(getCommand("desirepaths")).setExecutor(desirePathsCommand);
        Objects.requireNonNull(getCommand("desirepaths")).setTabCompleter(desirePathsCommand);

        // initialize the speedboosthandler
        // Bukkit.getPluginManager().registerEvents(new SpeedBoostHandler(this), this);

        // initialize togglemanager
        toggleManager = new ToggleManager(this);

        // Plugin startup logic
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (movementCheckEnabled && playerHasMoved) {
                    playerHandler(player, noBootsChance, leatherBootsChance, hasBootsChance, featherFallingChance,
                            ridingHorseChance, ridingBoatChance, ridingPigChance, sprintingBlockBelowChance,
                            sprintingBlockAtFeetChance, blockAtFeetSwitcherConfig, blockBelowSwitcherConfig);
                } else if (movementCheckEnabled) {
                    return;
                } else {
                    playerHandler(player, noBootsChance, leatherBootsChance, hasBootsChance, featherFallingChance,
                            ridingHorseChance, ridingBoatChance, ridingPigChance, sprintingBlockBelowChance,
                            sprintingBlockAtFeetChance, blockAtFeetSwitcherConfig, blockBelowSwitcherConfig);
                }
            }
        }, 0L, attemptFrequency);

        getServer().getPluginManager().registerEvents(this, this);

        Bukkit.getConsoleSender()
                .sendMessage(ChatColor.GOLD + ">>" + ChatColor.GREEN + " DesirePaths enabled successfully");

        // check if towny & worldguard are installed
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
    }

    private void playerHandler(Player player, int noBootsChance, int leatherBootsChance, int hasBootsChance,
            int featherFallingChance, int ridingHorseChance, int ridingBoatChance, int ridingPigChance,
            int sprintingBlockBelowChance, int sprintingBlockAtFeetChance, List<String> blockAtFeetSwitcherConfig,
            List<String> blockBelowSwitcherConfig) {
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
                        randomNum, sprintingBlockBelowChance, blockBelowSwitcherConfig));
        Bukkit.getScheduler().runTask(this,
                () -> blockHandler(player.getLocation().getBlock(), player, chance,
                randomNum, sprintingBlockAtFeetChance, blockAtFeetSwitcherConfig));
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
        if (player.getVehicle() instanceof Horse)
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
            List<String> switcherConfig) {
        if (disabledWorlds.contains(player.getWorld().getName())) {
            return;
        }
        if (worldGuardEnabled) {
            if (worldGuardIntegration.checkFlag(player)) {
                return;
            }
        }
        if (!townyEnabled || !pathsOnlyWherePlayerCanBreak) {
            // Run towny not enabled
            if (!player.isSprinting() && randomNum < chance) {
                blockSwitcher(block, switcherConfig);
            }
            if (player.isSprinting() && randomNum < chance + sprintingChance) {
                blockSwitcher(block, switcherConfig);
            }
        } else {
            // Run if towny is enabled and canBuild is true
            boolean canBuild = PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(),
                    TownyPermission.ActionType.DESTROY);
            if (canBuild) {
                if (!player.isSprinting() && randomNum < chance) {
                    blockSwitcher(block, switcherConfig);
                }
                if (player.isSprinting() && randomNum < chance + sprintingChance) {
                    blockSwitcher(block, switcherConfig);
                }
            }
        }
    }

    private void blockSwitcher(Block block, List<String> switcherConfig) {
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
        // initial config blockModifications lists
        blockBelowSwitcherConfig = getConfig().getStringList("blockModifications.blockBelowModifications");
        blockAtFeetSwitcherConfig = getConfig().getStringList("blockModifications.blockAtFeetModifications");
        // initial config townyModifiers booleans
        pathsOnlyWherePlayerCanBreak = getConfig().getBoolean("townyModifiers.pathsOnlyWherePlayerCanBreak");

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
