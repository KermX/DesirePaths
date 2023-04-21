package me.kermx.desirepaths.managers;

import me.kermx.desirepaths.DesirePaths;
import me.kermx.desirepaths.addons.Addon;
import me.kermx.desirepaths.addons.PlaceholderAddon;
import me.kermx.desirepaths.addons.TownyAddon;
import me.kermx.desirepaths.addons.WorldGuardAddon;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class AddonManager extends Manager {

    private final List<Addon> addons;

    public AddonManager(DesirePaths plugin) {
        super(plugin);
        this.addons = new ArrayList<>();
    }

    @Override
    public void setup() {
        for (Class<? extends Addon> clazz : Arrays.asList(PlaceholderAddon.class, TownyAddon.class, WorldGuardAddon.class)) {
            try {
                addons.add(clazz.getDeclaredConstructor(JavaPlugin.class).newInstance(plugin));
            } catch (Throwable ignored) {}
        }
        addons.removeIf(x -> !x.isEnabled());
    }

    public void preload() {
        addons.forEach(Addon::preLoad);
    }

    @Override
    public void load() {
        addons.forEach(Addon::load);
    }

    @SuppressWarnings("unchecked")
    public <T extends Addon> T getAddon(Class<T> clazz) {
        return addons.stream().filter(x -> x.getClass().equals(clazz)).map(x -> (T) x).findFirst().orElse(null);
    }

    public List<Addon> getAddons() {
        return this.addons;
    }
}
