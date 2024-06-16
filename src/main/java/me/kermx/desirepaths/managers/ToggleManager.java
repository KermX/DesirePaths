package me.kermx.desirepaths.managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.kermx.desirepaths.DesirePaths;

public class ToggleManager {

    private final DesirePaths plugin;
    private final Map<UUID, Boolean> toggleMap = new HashMap<>();
    private boolean maintenanceMode;
    private File toggleDataFile;
    private FileConfiguration toggleDataConfig;

    public ToggleManager(DesirePaths plugin) {
        this.plugin = plugin;
        this.toggleDataFile = new File(plugin.getDataFolder(), "toggleData.yml");

        // Load toggle states from the toggleData.yml file
        this.toggleDataConfig = YamlConfiguration.loadConfiguration(toggleDataFile);
        loadTogglesFromConfig();

        // Load maintenance mode from the toggleData.yml file
        this.maintenanceMode = toggleDataConfig.getBoolean("maintenanceMode", false);
    }

    public boolean getToggle(UUID playerId) {
        return toggleMap.getOrDefault(playerId, true);
    }

    public void setToggle(UUID playerId, boolean toggle) {
        toggleMap.put(playerId, toggle);
        toggleDataConfig.set(playerId.toString(), toggle);
        saveToggleData();
    }

    public boolean getMaintenanceMode(){
        return maintenanceMode;
    }

    public void setMaintenanceMode(boolean maintenanceMode){
        this.maintenanceMode = maintenanceMode;
        toggleDataConfig.set("maintenanceMode", maintenanceMode);
        saveToggleData();
    }

    private void loadTogglesFromConfig() {
        for (String key : toggleDataConfig.getKeys(false)) {
            // Skip the "maintenanceMode" key
            if (key.equals("maintenanceMode")) {
                continue;
            }
            try {
                UUID playerId = UUID.fromString(key);
                boolean toggleState = toggleDataConfig.getBoolean(key);
                toggleMap.put(playerId, toggleState);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Skipping invalid UUID string: " + key);
            }
        }
    }

    public boolean toggle(UUID playerId) {
        boolean currentToggle = getToggle(playerId);
        boolean newToggle = !currentToggle;
        setToggle(playerId, newToggle);
        return newToggle;
    }

    private void saveToggleData() {
        try {
            toggleDataConfig.save(toggleDataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save toggle data to " + toggleDataFile.getName());
        }
    }
}
