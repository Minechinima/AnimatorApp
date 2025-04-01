package com.minechinima.input.hotkey;

import com.minechinima.utils.IGenericDefaultRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.OperationNotSupportedException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Registry and factory for available {@link HotKeyConfig} types.
 */
public class HotKeyConfigRegistry implements IGenericDefaultRegistry<HotKeyConfig> {
    private final Map<Integer, Supplier<? extends HotKeyConfig>> registry = new HashMap<>();
    private final Map<Class<? extends HotKeyConfig>, Integer> idRegistry = new HashMap<>();
    private final Supplier<? extends HotKeyConfig> defaultConfig;

    public <T extends HotKeyConfig> HotKeyConfigRegistry(int id, Class<T> clazz, Supplier<T> defaultConfig) {
        this.register(id, clazz, defaultConfig);
        this.defaultConfig = defaultConfig;
    }

    /**
     * @param id
     * @param constructor
     * @throws OperationNotSupportedException when the given id is already registered.
     */
    @Override
    public <T extends HotKeyConfig> void register(int id, Class<T> clazz, Supplier<T> constructor) throws IllegalArgumentException {
        if (this.registry.containsKey(id) || this.idRegistry.containsKey(clazz)) {
            throw new IllegalArgumentException("The id is already registered");
        }

        this.registry.put(id, constructor);
        this.idRegistry.put(clazz, id);
    }

    @Override
    @Nullable
    public HotKeyConfig getNew(int id) {
        Supplier<? extends HotKeyConfig> constructor;
        if ((constructor = this.registry.get(id)) == null) return null;
        HotKeyConfig config = constructor.get();
        config.initChildren();
        return config;
    }

    @Override
    public Integer getID(HotKeyConfig value) {
        return this.idRegistry.get(value.getClass());
    }

    @Override
    public Map<Integer, HotKeyConfig> getAllNew() {
        Map<Integer, HotKeyConfig> map = new HashMap<>();
        for (Map.Entry<Integer, Supplier<? extends HotKeyConfig>> entry : this.registry.entrySet()) {
            HotKeyConfig config = entry.getValue().get();
            config.initChildren();
            map.put(entry.getKey(), config);
        }
        return map;
    }

    @Override
    @NotNull
    public HotKeyConfig getNewDefault() {
        HotKeyConfig defaultconfig = this.defaultConfig.get();
        defaultconfig.initChildren();
        return defaultconfig;
    }

    public LinkedHashSet<HotKeyConfig> getAvailableConfigs() {
        LinkedHashSet<HotKeyConfig> configs = new LinkedHashSet<>();
        for (Supplier<? extends HotKeyConfig> supplier : this.registry.values()) {
            HotKeyConfig config = supplier.get();
            config.initChildren();
            configs.add(config);
        }
        return configs;
    }
}
