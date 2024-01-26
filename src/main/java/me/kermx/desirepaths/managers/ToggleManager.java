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
    private File toggleDataFile;
    private FileConfiguration toggleDataConfig;

    public ToggleManager(DesirePaths plugin) {
        this.plugin = plugin;
        this.toggleDataFile = new File(plugin.getDataFolder(), "toggleData.yml");

        // Load toggle states from the toggleData.yml file
        this.toggleDataConfig = YamlConfiguration.loadConfiguration(toggleDataFile);
        loadTogglesFromConfig();
    }

    public boolean getToggle(UUID playerId) {
        return toggleMap.getOrDefault(playerId, true);
    }

    public void setToggle(UUID playerId, boolean toggle) {
        toggleMap.put(playerId, toggle);
        toggleDataConfig.set(playerId.toString(), toggle);
        saveToggleData();
    }

    private void loadTogglesFromConfig() {
        for (String playerIdString : toggleDataConfig.getKeys(false)) {
            UUID playerId = UUID.fromString(playerIdString);
            boolean toggleState = toggleDataConfig.getBoolean(playerIdString);
            toggleMap.put(playerId, toggleState);
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
