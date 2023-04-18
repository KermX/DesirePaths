package me.kermx.desirepaths.managers;

import me.kermx.desirepaths.DesirePaths;

import java.util.HashMap;
import java.util.Map;

public abstract class Manager {

    public static Map<Class<? extends Manager>, Manager> instances = new HashMap<>();

    public final DesirePaths plugin;

    public Manager(DesirePaths plugin) {
        this.plugin = plugin;
        instances.put(getClass(), this);
    }

    public abstract void load();

    public abstract void setup();

    public void close() {} // default

    public static <T extends Manager> T get(Class<T> clazz) {
        return clazz.cast(instances.get(clazz));
    }
}
